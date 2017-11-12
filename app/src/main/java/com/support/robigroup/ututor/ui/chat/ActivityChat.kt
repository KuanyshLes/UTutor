package com.support.robigroup.ututor.ui.chat

import android.os.Bundle
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.chat.ChatActivity
import com.support.robigroup.ututor.features.chat.custom.media.holders.CustomIncomingMessageViewHolder
import com.support.robigroup.ututor.features.chat.custom.media.holders.CustomOutcomingMessageViewHolder
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.features.main.MenuActivity
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.chat.ready.ReadyDialog
import kotlinx.android.synthetic.main.activity_chat.*
import javax.inject.Inject

class ActivityChat : BaseActivity(), ChatMvpView {

    lateinit var messagesList: MessagesList
    lateinit var messagesAdapter: MessagesListAdapter<ChatMessage>
    val imageLoader = ImageLoader { imageView, url -> Picasso.with(baseContext).load(url).into(imageView) }

    @Inject
    lateinit var mPresenter: ChatMvpPresenter<ChatMvpView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        activityComponent.inject(this)
        mPresenter.onAttach(this)

        setUp()
    }

    override fun setUp() {
        setSupportActionBar(toolbar)
        messagesList = findViewById(R.id.messagesList)
        val holders = MessageHolders()
                .registerContentType(
                        ChatActivity.CONTENT_TYPE_IMAGE_TEXT,
                        CustomIncomingMessageViewHolder::class.java,
                        R.layout.item_incoming_text_image_message,
                        CustomOutcomingMessageViewHolder::class.java,
                        R.layout.item_outcoming_text_image_message,
                        mPresenter)
        messagesAdapter = MessagesListAdapter(Constants.LEARNER_ID, holders, imageLoader)
        messagesAdapter.enableSelectionMode(mPresenter)
        text_finish.setOnClickListener { mPresenter.onFinishClick() }
        mPresenter.onViewInitialized()
    }

    override fun openMenuActivity() {
        MenuActivity.open(this)
        finish()
    }

    override fun startMenuActivity() {
        MenuActivity.open(this)
        finish()
    }

    override fun showFinishDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeFinishDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeReadyDialog() {
        val dialog = supportFragmentManager.findFragmentByTag(Constants.TAG_READY_DIALOG) as ReadyDialog?
        dialog?.dismiss()
    }

    override fun showReadyDialog(dif: Long) {
        val dialog = ReadyDialog()
        dialog.isCancelable = false
        dialog.startShow(supportFragmentManager, Constants.TAG_READY_DIALOG, dif)
    }

    override fun showEvalDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeEvalDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLearnerReadyDialog() {
        val dialog = supportFragmentManager.findFragmentByTag(Constants.TAG_READY_DIALOG) as ReadyDialog?
        dialog?.updateButtonText()
    }

    override fun changeCounterValueText(text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        mPresenter.onDetach()
        super.onDestroy()
    }
}
