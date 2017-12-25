package com.support.robigroup.ututor.ui.history

import android.media.MediaPlayer
import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.ChatHistory
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.ui.chat.AudioPlayerCallback
import com.support.robigroup.ututor.ui.chat.model.ChatMessage
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class HistoryPresenter<V : HistoryMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable), HistoryMvpPresenter<V> {


    private lateinit var mChatHistory: ChatHistory
    private var audioCallback: AudioPlayerCallback? = null

    override fun onViewInitialized() {
        mChatHistory = mvpView.getChatHistory()
        requestMessages(mChatHistory.Id!!)
    }

    private fun requestMessages(chatId: Int){
        val subscription = dataManager.apiHelper.getHistoryMessages(chatId)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe {
                    mvpView.showLoading()
                }
                .doAfterTerminate {
                    mvpView.hideLoading()
                }
                .subscribe(
                        { message ->
                            if(message.isSuccessful){
                                val messages = message.body()
                                if(messages!=null && messages.isNotEmpty()){
                                    mvpView.addMessages(messages)
                                }
                            }else{
                                handleApiError(ANError(message.raw()))
                            }
                        },
                        { e ->
                            handleApiError(ANError(e))
                        }
                )
        compositeDisposable.add(subscription)
    }

    override fun hasContentFor(message: ChatMessage, type: Byte): Boolean {
        return Functions.hasContentFor(message, type)
    }

    override fun onMessageClick(message: ChatMessage) {
        if(Functions.hasContentFor(message, Constants.CONTENT_TYPE_IMAGE_TEXT))
            mvpView.showImage(message.imageUrl)
    }

    override fun onDetach() {
        audioCallback?.onComplete()
        dataManager.cleanDirectories()
        super.onDetach()
    }

    //play presenter
    override fun provideDataManager(): DataManager {
        return dataManager
    }

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
}