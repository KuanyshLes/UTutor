package com.support.robigroup.ututor.ui.history

import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.support.robigroup.ututor.commons.ChatHistory
import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView
import com.support.robigroup.ututor.ui.chat.PlayPresenter
import com.support.robigroup.ututor.ui.chat.PlayView
import com.support.robigroup.ututor.ui.chat.model.ChatMessage

interface HistoryMvpView: MvpView, PlayView {
    fun showImage(url: String)
    fun setToolbarTitle(title: String)
    fun getChatHistory(): ChatHistory
    fun addMessages(messages: List<ChatMessage>)
}

interface HistoryMvpPresenter<V : HistoryMvpView> : MvpPresenter<V>,
        MessageHolders.ContentChecker<ChatMessage>,
        PlayPresenter,
        MessagesListAdapter.OnMessageClickListener<ChatMessage>{
    fun onViewInitialized()
}
