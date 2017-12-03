package com.support.robigroup.ututor.ui.chat

import android.media.MediaPlayer
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.contentmanager.ContentManager
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.base.DialogMvpView
import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView


interface ChatMvpView : MvpView, AudioView, HoldingButtonView{

    fun setToolbarTitle(title: String)

    fun showFinishDialog()

    fun showReadyDialog(dif: Long)

    fun closeReadyDialog()

    fun onLearnerReadyDialog()

    fun showEvalDialog()

    fun startMenuActivity()

    fun onCancelImageLoad()

    fun notifyItemRangeInserted(messages: List<ChatMessage>, startIndex: Int,rangeLength: Int)

}


interface ChatMvpPresenter<V : ChatMvpView> : MvpPresenter<V>,
        MessageInput.InputListener,
        MessageHolders.ContentChecker<ChatMessage>,
        ContentManager.PickContentListener,
        AudioPresenter,
        HoldingButtonLayoutListener,
        MediaPlayer.OnCompletionListener{

    fun onFinishClick()

    fun onOkFinishClick()

    fun onReadyClick()

    fun onViewInitialized()

    fun onCounterFinish()

}



interface AudioPresenter {
    fun onPlayClick(message: ChatMessage)
    fun onPauseClick()
    fun onPlayFinish()
    fun setPlayerCallback(callback: AudioPlayerCallback)
    fun getPlayerCurrentPosition(): Long
    fun stopPrevious()
}

interface AudioView {
    fun startRecord(filePath: String)
    fun stopRecord()
    fun startPlay(filePath: String)
    fun pausePlay()
    fun stopPlay()
    fun getCurrentPlayingTime(): Long
    fun getAudioPresenter(): AudioPresenter
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

