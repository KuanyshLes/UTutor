package com.support.robigroup.ututor.ui.chat

import android.media.MediaPlayer
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.contentmanager.ContentManager
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.data.play_record.AudioItem
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.base.DialogMvpView
import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView
import omrecorder.PullableSource
import omrecorder.Recorder
import java.io.File


interface ChatMvpView : MvpView, PlayView, RecordView, HoldingButtonView{

    fun setToolbarTitle(title: String)

    fun showFinishDialog()

    fun showReadyDialog(dif: Long)

    fun closeReadyDialog()

    fun onLearnerReadyDialog()

    fun showEvalDialog()

    fun startMenuActivity()

    fun onCancelImageLoad()

    fun notifyItemRangeInserted(messages: List<ChatMessage>, startIndex: Int, rangeLength: Int)
    fun notifyItemRangeUpdated(messages: List<ChatMessage>, startIndex: Int, rangeLength: Int)
    fun notifyItemRangeDeleted(messages: List<ChatMessage>, startIndex: Int, rangeLength: Int)

    fun showImage(url: String)
}


interface ChatMvpPresenter<V : ChatMvpView> : MvpPresenter<V>,
        MessageInput.InputListener,
        MessageHolders.ContentChecker<ChatMessage>,
        ContentManager.PickContentListener,
        PlayPresenter,
        HoldingButtonLayoutListener,
        MessagesListAdapter.OnMessageClickListener<ChatMessage>{

    fun onFinishClick()

    fun onOkFinishClick()

    fun onReadyClick()

    fun onViewInitialized()

    fun onCounterFinish()

}



interface PlayPresenter : MediaPlayer.OnCompletionListener{
    fun onPlayClick(message: ChatMessage)
    fun onPauseClick()
    fun onPlayerPrepared()
    fun setPlayerCallback(callback: AudioPlayerCallback)
    fun getPlayerCurrentPosition(): Int
    fun stopPrevious()
}

interface PlayView {
    fun preparePlay(filePath: String)
    fun startPlay()
    fun pausePlay()
    fun stopPlay()
    fun getPlayDuration(): Int
    fun getCurrentPlayingTime(): Int
    fun getPlayPresenter(): PlayPresenter
}

interface RecordView{
    fun startRecord()
    fun stopRecord()
    fun setupRecorder(filePath: String)
    fun getFilePath(): String
    fun getMic(): PullableSource
}

interface AudioPlayerCallback {
    fun onNewPlay()
    fun onProgressChanged(cDur: Long, tDur: Long)
    fun onComplete()
    fun onReady(duration: Int)
}

interface HoldingButtonView{
    fun startTimer()
    fun getStartTime(): Long
    fun stopTimer()
    fun showCancelled()
    fun showSubmitted()
    fun cancelAnimations()
    fun startCollapseAnimations()
    fun startExpandAnimations()
    fun moveSlideToCancel(offset: Float, isCancel: Boolean)
}




interface RateMvpView : DialogMvpView{

    fun initViews(info: ChatInformation)

}

interface RateMvpPresenter<V: RateMvpView>: MvpPresenter<V>{

    fun onClickRateButton(rating: Float)

    fun onViewInitialized()

}

