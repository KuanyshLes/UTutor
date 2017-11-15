package com.support.robigroup.ututor.ui.chat

import android.os.Bundle
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.contentmanager.ContentManager
import com.stfalcon.frescoimageviewer.ImageViewer
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.chat.custom.media.holders.CustomIncomingMessageViewHolder
import com.support.robigroup.ututor.features.chat.custom.media.holders.CustomOutcomingMessageViewHolder
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.features.main.MenuActivity
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.chat.eval.RateDialog
import com.support.robigroup.ututor.ui.chat.ready.ReadyDialog
import kotlinx.android.synthetic.main.activity_chat.*
import javax.inject.Inject

class ActivityChat : BaseActivity(), ChatMvpView {

    lateinit var messagesList: MessagesList
    lateinit var messagesAdapter: MessagesListAdapter<ChatMessage>
    private val imageLoader = ImageLoader { imageView, url -> Picasso.with(baseContext).load(url).into(imageView) }
    lateinit var contentManager: ContentManager

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
                        Constants.CONTENT_TYPE_IMAGE_TEXT,
                        CustomIncomingMessageViewHolder::class.java,
                        R.layout.item_incoming_text_image_message,
                        CustomOutcomingMessageViewHolder::class.java,
                        R.layout.item_outcoming_text_image_message,
                        mPresenter)
        messagesAdapter = MessagesListAdapter(Constants.LEARNER_ID, holders, imageLoader)
        messagesAdapter.enableSelectionMode(mPresenter)
        messagesAdapter.setOnMessageClickListener {
            message: ChatMessage ->
            if(message.filePath!=null)
                ImageViewer.Builder(this, arrayOf(message.imageUrl))
                        .setStartPosition(0)
                        .show()
        }

        contentManager = ContentManager(this, mPresenter)
        text_finish.setOnClickListener { mPresenter.onFinishClick() }
        mPresenter.onViewInitialized()
    }

    override fun startMenuActivity() {
        MenuActivity.open(this)
        finish()
    }

    override fun showFinishDialog() {

    }

    override fun closeFinishDialog() {

    }

    override fun closeReadyDialog() {
        val dialog = supportFragmentManager.findFragmentByTag(Constants.TAG_READY_DIALOG) as ReadyDialog?
        dialog?.dismiss()
    }

    override fun showReadyDialog(dif: Long) {
        val dialog = ReadyDialog()
        dialog.isCancelable = false
        dialog.startShow(supportFragmentManager, Constants.TAG_READY_DIALOG, dif, mPresenter)
    }

    override fun showEvalDialog() {
        val ft = supportFragmentManager.beginTransaction()
        val prev = supportFragmentManager.findFragmentByTag(Constants.TAG_RATE_DIALOG)
        if (prev != null) {
            ft.remove(prev)
            ft.commit()
            supportFragmentManager.popBackStack()
        }
        RateDialog().show(ft, Constants.TAG_RATE_DIALOG)
    }

    override fun onCancelImageLoad() {
        showMessage(R.string.error_cancelled)
    }

    override fun onLearnerReadyDialog() {
        val dialog = supportFragmentManager.findFragmentByTag(Constants.TAG_READY_DIALOG) as ReadyDialog?
        dialog?.updateButtonText()
    }

    override fun notifyItemRangeInserted(messages: List<ChatMessage>, startIndex: Int,rangeLength: Int){
        for(i in startIndex until startIndex+rangeLength){
            messagesAdapter.addToStart(messages[i],true)
        }
    }

    override fun onDestroy() {
        mPresenter.onDetach()
        super.onDestroy()
    }
}
