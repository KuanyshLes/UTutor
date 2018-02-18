package com.support.robigroup.ututor.ui.chat

import android.media.MediaPlayer
import android.net.Uri
import com.androidnetworking.error.ANError
import com.stfalcon.contentmanager.ContentManager
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.ui.chat.model.ChatMessage
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.io.File
import javax.inject.Inject


class ChatPresenter<V : ChatMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable), ChatMvpPresenter<V> {


    private var audioCallback: AudioPlayerCallback? = null

    override fun onViewInitialized() {

        val surnameName = chatInformation.Teacher.split(" ")
        if (surnameName.size == 2)
            mvpView.setToolbarTitle(surnameName[1])
        else {
            mvpView.setToolbarTitle(surnameName[0])
        }

        val info = chatInformation
        if (!info.TeacherReady || !info.LearnerReady) {
            val dif = Functions.getDifferenceInMillis(info.deviceCreateTime!!)
            if (dif > 500 && dif < Constants.WAIT_TIME) {
                mvpView.showReadyDialog(dif)
            } else {
                mvpView.startMenuActivity()
            }
        } else {
            updateChatMessages()
        }

        chatInformation.addChangeListener<ChatInformation> { rs, changeset ->
            if (changeset == null) {
                logd("chnage set is null")
            } else {
                if (changeset.isDeleted) {
                    logd("chnage set is deleted")
                } else if (!rs.isLoaded) {
                    logd("object is not loaded")
                } else if (!rs.isValid) {
                    logd("object is not valid")
                } else {
                    if (rs.StatusId == Constants.STATUS_COMPLETED) {
                        if(isViewAttached)
                            mvpView.showEvalDialog()
                    } else if (rs.TeacherReady && rs.LearnerReady) {
                        mvpView.closeReadyDialog()
                    } else if (rs.LearnerReady && !rs.TeacherReady) {
                        mvpView.onLearnerReadyDialog()
                    }
                }
            }
        }

        chatMessages.addChangeListener { messages, changeSet ->
            if (changeSet == null) {
                mvpView.notifyItemRangeInserted(messages, 0, messages.size - 1)
            } else {
                val insertions = changeSet.insertionRanges
                for (range in insertions) {
                    if (mvpView != null) {
                        mvpView.notifyItemRangeInserted(messages, range.startIndex, range.length)
                    }
                }
            }
        }
    }



    override fun onFinishClick() {
        mvpView.showFinishDialog()
    }

    override fun onReadyClick() {
        compositeDisposable.add(dataManager.apiHelper.postChatReady()
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        val res = response.body()?.charStream()?.readText()
                        if (res != null) {
                            realm.executeTransaction {
                                if (res == "ready") {
                                    chatInformation.LearnerReady = true
                                    chatInformation.TeacherReady = true
                                } else {
                                    chatInformation.LearnerReady = true
                                }
                            }
                        } else {
                            realm.executeTransaction {
                                chatInformation.LearnerReady = true
                            }
                        }
                    } else {
                        handleApiError(ANError(response.raw()))
                    }
                }, { error ->
                    handleApiError(ANError(error))
                }))
    }

    override fun onCounterFinish() {
        mvpView.startMenuActivity()
    }

    override fun onOkFinishClick() {
        compositeDisposable.add(dataManager.apiHelper.postChatComplete()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { response ->
                            if (response.isSuccessful) {
                                updateChatInformation(response.body())
                            } else {
                                mvpView.startMenuActivity()
                            }
                        },
                        { error ->
                            handleApiError(ANError(error))
                        }
                ))
    }

    override fun onSubmit(input: CharSequence?): Boolean {
        compositeDisposable.add(dataManager.sendImageTextMessage(messageText = input.toString())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { messageResponse ->
                            if (messageResponse.isSuccessful) {
                                val message: ChatMessage? = messageResponse.body()
                                if (message != null) {
                                    realm.executeTransaction {
                                        realm.insert(message)
                                    }
                                } else {
                                    handleApiError(null)
                                }
                            } else {
                                handleApiError(ANError(messageResponse.raw()))
                            }
                        },
                        { e ->
                            handleApiError(ANError(e))
                        }
                )
        )
        return true
    }

    override fun hasContentFor(message: ChatMessage, type: Byte): Boolean {
        return Functions.hasContentFor(message, type)
    }

    override fun onMessageClick(message: ChatMessage) {
        if (Functions.hasContentFor(message, Constants.CONTENT_TYPE_IMAGE_TEXT))
            mvpView.showImage(message.imageUrl)
    }

    override fun onChatFinished() {
        dataManager.cleanDirectories()
        realm.executeTransaction {
            realm.where(ChatInformation::class.java).findAll().deleteAllFromRealm()
            realm.where(ChatMessage::class.java).findAll().deleteAllFromRealm()
        }
    }

    override fun onDetach() {
        audioCallback?.onComplete()
        chatInformation?.removeAllChangeListeners()
        chatMessages?.removeAllChangeListeners()
        super.onDetach()
    }

    //methods for play presenter
    override fun onCompletion(p0: MediaPlayer?) {
        audioCallback?.onComplete()
    }

    override fun stopPrevious() {
        mvpView.stopPlay()
        audioCallback?.onComplete()
    }

    override fun resumePlay() {
        mvpView.startPlay()
    }

    override fun onPlayerPrepared() {
        audioCallback?.onReady(mvpView.getPlayDuration())
        mvpView.startPlay()
    }

    override fun onPlayClick(message: ChatMessage) {
        audioCallback?.onNewPlay()
        mvpView.preparePlay(message.localFilePath)
    }

    override fun onPauseClick() {
        mvpView.pausePlay()
    }

    override fun setPlayerCallback(callback: AudioPlayerCallback) {
        audioCallback = callback
    }

    override fun getPlayerCurrentPosition(): Int? {
        return mvpView.getCurrentPlayingTime()
    }

    override fun onSeekChanged(progress: Int) {
        mvpView.seekTo(progress)
    }

    override fun provideDataManager(): DataManager {
        return dataManager
    }

    //callbacks from holding button events
    override fun onBeforeExpand() {
        logd("onBeforeExpand")
        mvpView.cancelAnimations()
        mvpView.startExpandAnimations() // TODO change to something logical
    }

    override fun onExpand() {
        logd("onExpand")
        mvpView.stopPlay()
        audioCallback?.onComplete()
        audioCallback = null
        mvpView.startTimer()
        mvpView.startRecord(dataManager.getSentSavePath(chatInformation.Id.toString()))
    }

    override fun onBeforeCollapse() {
        logd("onBeforeCollapse")
        mvpView.cancelAnimations()
        mvpView.startCollapseAnimations()
    }

    override fun onCollapse(isCancel: Boolean) {
        logd("onCollapse")
        mvpView.stopTimer()
        try {
            mvpView.stopRecord()
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
        if (isCancel) {
            mvpView.showCancelled()
        } else {
            val duarationms = System.currentTimeMillis() - mvpView.getStartTime()
            if (duarationms / 1000.0 > 1.0) {
                sendAudioMessage(mvpView.getFilePath())
                mvpView.showSubmitted()
            }
        }
    }

    override fun onOffsetChanged(offset: Float, isCancel: Boolean) {
        mvpView.moveSlideToCancel(offset, isCancel)
    }

    //methods to add image from gallery or other resources
    override fun onStartContentLoading() {

    }

    override fun onError(error: String?) {
        handleApiError(ANError(error))
    }

    override fun onCanceled() {
        mvpView.onCancelImageLoad()
    }

    override fun onContentLoaded(uri: Uri?, contentType: String?) {
        if (contentType.equals(ContentManager.Content.IMAGE.toString())) {
            if (uri != null) {
                sendImageMessage(uri.path)
            }
        }
    }

    private fun updateChatMessages() {
        compositeDisposable.add(dataManager.apiHelper.getChatMessages()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe {
                    mvpView.showLoading()
                }
                .doAfterTerminate {
                    mvpView.hideLoading()
                }
                .subscribe(
                        { messageResponse ->
                            if (messageResponse.isSuccessful) {
                                val messages: List<ChatMessage>? = messageResponse.body()
                                if (messages != null) {
                                    for (message in messages) {
                                        for (chatMessage in chatMessages) {
                                            if (message.id == chatMessage.id) {
                                                message.localFilePath = chatMessage.localFilePath
                                                break
                                            }
                                        }
                                    }
                                    realm.executeTransaction { r ->
                                        chatMessages.deleteAllFromRealm()
                                        r.insert(messages)
                                    }
                                } else {
                                    handleApiError(null)
                                }
                            } else {
                                handleApiError(ANError(messageResponse.raw()))
                            }
                        },
                        { e ->
                            handleApiError(ANError(e))
                        }
                ))
    }

    private fun sendImageMessage(imageUri: String) {
        val encodedImage = Functions.getEncodedImage(imageUri)
        if (encodedImage != null) {
            compositeDisposable.add(dataManager.sendImageTextMessage(file64base = encodedImage)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            { messageResponse ->
                                if (messageResponse.isSuccessful) {
                                    val message: ChatMessage? = messageResponse.body()
                                    if (message != null) {
                                        realm.executeTransaction {
                                            realm.copyToRealm(message)
                                        }
                                    } else {
                                        handleApiError(null)
                                    }
                                } else {
                                    handleApiError(ANError(messageResponse.raw()))
                                }
                            },
                            { e ->
                                handleApiError(ANError(e))
                            }
                    )
            )
        }
    }

    private fun sendAudioMessage(fileUri: String) {
        val file = File(fileUri)
        if (file.exists() && file.isFile) {
            compositeDisposable.add(dataManager.sendAudioMessage(file)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            { messageResponse ->
                                if (messageResponse.isSuccessful) {
                                    val message: ChatMessage? = messageResponse.body()
                                    if (message != null) {
                                        message.localFilePath = fileUri
                                        realm.executeTransaction {
                                            realm.copyToRealm(message)
                                        }
                                    } else {
                                        handleApiError(null)
                                    }
                                } else {
                                    handleApiError(ANError(messageResponse.raw()))
                                }
                            },
                            { e ->
                                handleApiError(ANError(e))
                            }
                    )
            )
        } else {

        }
    }


}
