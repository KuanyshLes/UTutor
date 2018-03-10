package com.support.robigroup.ututor.ui.navigationDrawer.history

import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.support.robigroup.ututor.commons.ChatHistory
import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView
import com.support.robigroup.ututor.ui.chat.PlayPresenter
import com.support.robigroup.ututor.ui.chat.PlayView
import com.support.robigroup.ututor.ui.chat.model.ChatMessage

interface HistoryChatMessagesMvpView: MvpView, PlayView {
    fun showImage(url: String)
    fun setToolbarTitle(title: String)
    fun getChatHistory(): ChatHistory
    fun addMessages(messages: List<ChatMessage>)
}

interface HistoryChatMessagesMvpPresenter<V : HistoryChatMessagesMvpView> : MvpPresenter<V>,
        MessageHolders.ContentChecker<ChatMessage>,
        PlayPresenter,
        MessagesListAdapter.OnMessageClickListener<ChatMessage>{
    fun onViewInitialized()
}

interface ChatsListMvpView: MvpView{
    fun setSwipeRefresh(refresh: Boolean)
    fun updateChats(chats: List<ChatHistory>?)
    fun onHistoryItemClicked(item: ChatHistory)
}

interface ChatListMvpPresenter<V : ChatsListMvpView>: MvpPresenter<V>{
    fun onViewInitialized()
    fun onRefreshList()

}