package com.support.robigroup.ututor.ui.chat

import android.media.MediaPlayer
import android.net.Uri
import com.androidnetworking.error.ANError
import com.stfalcon.contentmanager.ContentManager
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Response
import java.io.File
import java.util.*
import javax.inject.Inject


class ChatPresenter<V : ChatMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable), ChatMvpPresenter<V> {


    private var audioCallback: AudioPlayerCallback? = null

    override fun onViewInitialized() {

        mvpView.setToolbarTitle(chatInformation.Teacher)

        chatInformation.addChangeListener<ChatInformation> {
            rs, changeset ->
            if(changeset==null){
                logd("chnage set is null")
            }else{
                if(changeset.isDeleted){
                    logd("chnage set is deleted")
                }else if(!rs.isLoaded){
                    logd("object is not loaded")
                }else if(!rs.isValid){
                    logd("object is not valid")
                }else{
                    if(rs.StatusId == Constants.STATUS_COMPLETED){
                        mvpView.showEvalDialog()
                    }else if(rs.TeacherReady&&rs.LearnerReady){
                        mvpView.closeReadyDialog()
                    }else if(rs.LearnerReady&&!rs.TeacherReady){
                        mvpView.onLearnerReadyDialog()
                    }
                }
            }
        }

        val info = chatInformation
        if(!info.TeacherReady||!info.LearnerReady){
            val dif = Functions.getDifferenceInMillis(info.CreateTime)
            val utc = dif - 6*60*60*1000
            logd(utc.toString())
            if((dif>1000&&dif<Constants.WAIT_TIME)||(utc>1000&&utc<Constants.WAIT_TIME)){
                mvpView.showReadyDialog(dif+1000)
            }else{
                mvpView.startMenuActivity()
            }
        }




        chatMessages.addChangeListener {
            messages, changeSet ->
            if (changeSet == null) {
                mvpView.notifyItemRangeInserted(messages,0,messages.size-1)
            }else{
                val insertions = changeSet.insertionRanges
                for (range in insertions) {
                    if(mvpView==null){
                        logd("mvpview is null")
                        logd("hashcode from inside null : "+ this.hashCode().toString())
                    }else{
                        mvpView.notifyItemRangeInserted(messages, range.startIndex, range.length)
                        logd("hashcode from inside not null : "+ this.hashCode().toString())

                    }
                }
            }
        }
        mvpView.notifyItemRangeInserted(chatMessages, 0, chatMessages.size-1)
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
                            val info = chatInformation
                            realm.executeTransaction {
                                if (res == "ready") {
                                    info.LearnerReady = true
                                    info.TeacherReady = true
                                } else {
                                    info.LearnerReady = true
                                }
                            }
                        } else {
                            handleApiError(null)
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
                            if(response.isSuccessful){
                                updateChatInformation(response.body())
                                mvpView.showEvalDialog()
                            }else{
                                mvpView.startMenuActivity()
                            }
                        },
                        { error ->
                            handleApiError(ANError(error))
                        }
                ))
    }

    override fun onSubmit(input: CharSequence?): Boolean {
        compositeDisposable.add(sendMessage(messageText = input.toString())
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { messageResponse ->
                            if(messageResponse.isSuccessful){
                                val message: ChatMessage? = messageResponse.body()
                                if(message!=null){
                                    realm.executeTransaction {
                                        realm.insert(message)
                                    }
                                }else{
                                    handleApiError(null)
                                }
                            }else{
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
        if(Functions.hasContentFor(message, Constants.CONTENT_TYPE_IMAGE_TEXT))
            mvpView.showImage(message.imageUrl)
    }


    //methods for play presenter
    override fun onCompletion(p0: MediaPlayer?) {
        audioCallback?.onComplete()
    }

    override fun stopPrevious() {
        mvpView.stopPlay()
    }

    override fun onPlayerPrepared() {
        audioCallback?.onReady(mvpView.getPlayDuration())
        mvpView.startPlay()
    }

    override fun onPlayClick(message: ChatMessage) {
        audioCallback?.onNewPlay()
        mvpView.preparePlay(getSavePath(chatInformation.Id!!.toString(),message.id.toString()))
    }

    override fun onPauseClick() {
        mvpView.pausePlay()
    }

    override fun onPlayFinish() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setPlayerCallback(callback: AudioPlayerCallback) {
        audioCallback = callback
    }

    override fun getPlayerCurrentPosition(): Long {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun getSavePath(chatId: String, messageId: String): String {
        val path = Constants.BASE_AUDIO_FOLDER + chatId + "/"
        File(path).mkdirs()
        return Constants.BASE_AUDIO_FOLDER + chatId + "/" + messageId+
                "audio.3gp"
    }

    //callbacks from holding button events
    override fun onBeforeExpand() {
        mvpView.cancelAnimations()
        mvpView.startExpandAnimations()
        mvpView.setupRecorder(getSavePath(chatInformation.Id!!.toString(),(chatMessages.last()!!.id.toInt()+1).toString()))
    }

    override fun onExpand() {
        mvpView.startTimer()
        audioCallback?.onNewPlay()
        audioCallback = null
        mvpView.startRecord()
    }

    override fun onBeforeCollapse() {
        mvpView.cancelAnimations()
        mvpView.startCollapseAnimations()
    }

    override fun onCollapse(isCancel: Boolean) {
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
                sendImageMessage(mvpView.getFilePath())
                mvpView.showSubmitted()
            }
        }
    }

    override fun onOffsetChanged(offset: Float, isCancel: Boolean) {
        mvpView.moveSlideToCancel(offset, isCancel)
    }


    //methods to add image from gallery or other resources
    override fun onStartContentLoading() {
        TODO("not implemented")
    }

    override fun onError(error: String?) {
        handleApiError(ANError(error))
    }

    override fun onCanceled() {
        mvpView.onCancelImageLoad()
    }

    override fun onContentLoaded(uri: Uri?, contentType: String?) {
        if (contentType.equals(ContentManager.Content.IMAGE.toString())) {
            if(uri!=null){
                sendImageMessage(uri.path)
            }
        }
    }

    private fun sendImageMessage(imageUri: String){
        val encodedImage = Functions.getEncodedImage(imageUri)
        if(encodedImage!=null){
            compositeDisposable.add(sendMessage(file64base = encodedImage)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            { messageResponse ->
                                if(messageResponse.isSuccessful){
                                    val message: ChatMessage? = messageResponse.body()
                                    if(message!=null){
                                        realm.executeTransaction {
                                            realm.copyToRealmOrUpdate(message)
                                        }
                                    }else{
                                        handleApiError(null)
                                    }
                                }else{
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

    private fun sendMessage(messageText: String? = null, file64base: String? = null): Flowable<Response<ChatMessage>> =
            if(file64base != null&&messageText!=null){
                val res: HashMap<String,String> = HashMap()
                res.put("File",file64base)
                res.put("Message",messageText)
                dataManager.apiHelper.postMessagePhoto(res)
            } else if(file64base!=null) {
                val res: HashMap<String,String> = HashMap()
                res.put("File",file64base)
                dataManager.apiHelper.postMessagePhoto(res)
            }else if(messageText!=null) dataManager.apiHelper.postTextMessage(messageText)
            else Flowable.empty()
}
