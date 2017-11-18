package com.support.robigroup.ututor.ui.chat

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
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

    private lateinit var messagesList: MessagesList
    private lateinit var messagesAdapter: MessagesListAdapter<ChatMessage>
    private lateinit var contentManager: ContentManager
    private lateinit var menu: Menu
    private var selectionCount: Int = 0
    private val imageLoader = ImageLoader { imageView, url -> Picasso.with(baseContext).load(url).into(imageView) }

    @Inject
    lateinit var mPresenter: ChatMvpPresenter<ChatMvpView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        setUp()
        mPresenter.onViewInitialized()
    }

    override fun setUp() {
        setSupportActionBar(toolbar)

        val input = findViewById<MessageInput>(R.id.input)
        input.setInputListener(mPresenter)
        input.setAttachmentsListener({
            AlertDialog.Builder(this)
                    .setItems(R.array.view_types_dialog, (
                            DialogInterface.OnClickListener{ p0, order -> when (order) {
                                0 -> contentManager.pickContent(ContentManager.Content.IMAGE)}})
                    ).show()
        })

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
        messagesAdapter.enableSelectionMode({
            count ->
            selectionCount = count
            menu.findItem(R.id.action_delete).isVisible = count > 0
            menu.findItem(R.id.action_copy).isVisible = count > 0
        })
        messagesAdapter.setOnMessageClickListener {
            message: ChatMessage ->
            if(message.filePath!=null)
                ImageViewer.Builder(this, arrayOf(message.imageUrl))
                        .setStartPosition(0)
                        .show()
        }
        messagesList.setAdapter(messagesAdapter)

        contentManager = ContentManager(this, mPresenter)
        text_finish.setOnClickListener { mPresenter.onFinishClick() }
    }

    override fun setToolbarTitle(title: String) {
        teacher_name_title.text = title
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chat_actions_menu, menu)
        this.menu = menu
        messagesAdapter.unselectAllItems()
        return true
    }

    override fun startMenuActivity() {
        MenuActivity.open(this)
        finish()
    }

    override fun showFinishDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.prompt_are_you_sure))
                .setCancelable(false)
                .setPositiveButton("OK")
                {
                    dialog, id ->
                    dialog.cancel()
                    mPresenter.onOkFinishClick()
                }
                .setNegativeButton("Cancel")
                {
                    dialog, id ->
                    dialog.cancel()
                }
        val alert = builder.create()
        alert.setCancelable(true)
        alert.show()
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

    override fun onFragmentDetached(tag: String?) {
        if(tag!=null){
            when(tag){
                Constants.TAG_RATE_DIALOG ->
                        startMenuActivity()
            }
        }
        super.onFragmentDetached(tag)
    }

    override fun onDestroy() {
        mPresenter.onDetach()
        super.onDestroy()
    }

    companion object {
        fun open(context: Context) {
            val intent = Intent(context, ActivityChat::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }
}
