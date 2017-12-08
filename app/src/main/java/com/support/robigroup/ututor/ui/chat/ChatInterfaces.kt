package com.support.robigroup.ututor.ui.chat

import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.contentmanager.ContentManager
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.base.DialogMvpView
import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView


interface ChatMvpView : MvpView, AudioHolderListener {

    fun setToolbarTitle(title: String)

    fun showFinishDialog()

    fun showReadyDialog(dif: Long)

    fun closeReadyDialog()

    fun onLearnerReadyDialog()

    fun showEvalDialog()

    fun startMenuActivity()

    fun onCancelImageLoad()

    fun notifyItemRangeInserted(messages: List<ChatMessage>, startIndex: Int,rangeLength: Int)

    fun showImage(url: String)
}


interface ChatMvpPresenter<V : ChatMvpView> : MvpPresenter<V>,
        MessageInput.InputListener,
        MessageHolders.ContentChecker<ChatMessage>,
        ContentManager.PickContentListener,
        MessagesListAdapter.OnMessageClickListener<ChatMessage>{

    fun onFinishClick()

    fun onOkFinishClick()

    fun onReadyClick()

    fun onViewInitialized()

    fun onCounterFinish()

    fun getSavePath(): String

}

interface AudioHolderListener {
    fun onPlayClick(fileUrl: String)
    fun onPauseClick()
    fun onSeekChanged()
    fun setPlayerCallback(callback: AudioPlayerCallback)
    fun stopPrevious()
    fun getPlayerCurrentPosition(): Long
}


interface AudioPlayerCallback {
    fun onNewPlay()
    fun onProgressChanged(cDur: Long, tDur: Long)
    fun onComplete()
    fun onReady(duration: Int)
}


interface RateMvpView : DialogMvpView{

    fun initViews(info: ChatInformation)

}

interface RateMvpPresenter<V: RateMvpView>: MvpPresenter<V>{

    fun onClickRateButton(rating: Float)

    fun onViewInitialized()

}

