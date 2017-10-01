package com.support.robigroup.ututor.screen.history

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.util.Log
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.contentmanager.ContentManager
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.screen.chat.model.MyMessage
import com.support.robigroup.ututor.screen.chat.model.User
import io.reactivex.disposables.CompositeDisposable
import kotlin.properties.Delegates
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.screen.chat.custom.media.holders.CustomIncomingMessageViewHolder
import com.support.robigroup.ututor.screen.chat.custom.media.holders.CustomOutcomingMessageViewHolder
import android.view.Menu
import android.view.MenuItem
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.commons.toast
import com.support.robigroup.ututor.model.content.ChatHistory
import com.support.robigroup.ututor.screen.chat.model.CustomMessageHistory
import com.support.robigroup.ututor.screen.chat.model.MyHistoryMessage
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history_list.*
import java.text.SimpleDateFormat
import java.util.*


class HistoryList : AppCompatActivity(),
        MessagesListAdapter.SelectionListener,
        MessageHolders.ContentChecker<MyMessage>{


    private var messagesList: MessagesList? = null
    private var mAdapter: MessagesListAdapter<MyHistoryMessage>? = null
    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private lateinit var mChatHistory: ChatHistory
    private var menu: Menu? = null
    private var selectionCount: Int = 0
    private var imageLoader: ImageLoader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_list)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        mChatHistory = intent.getParcelableExtra(ARG_CHAT_HISTORY)


        imageLoader = ImageLoader { imageView, url -> Picasso.with(baseContext).load(url).into(imageView) }
        messagesList = findViewById(R.id.messagesList)

        mAdapter = MessagesListAdapter("Learner", imageLoader)
        mAdapter!!.enableSelectionMode(this)
        messagesList!!.setAdapter(mAdapter)

        supportActionBar?.title = mChatHistory.ChatUserName
        requestMessages(mChatHistory.Id!!)
    }

    private fun requestMessages(chatId: Int){
        val subscription = MainManager().getHistoryMessages(chatId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { message ->
                            if(requestErrorHandler(message.code(),message.message())){
                                mAdapter?.addToEnd(message.body()?.map { Functions.getMyMessageHistory(it) },true)
                            }
                        },
                        { e ->
                            Log.e("Error",e.stackTrace.toString())
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        compositeDisposable.add(subscription)
    }

    override fun hasContentFor(message: MyMessage, type: Byte): Boolean {
        when (type) {
            ChatActivity.CONTENT_TYPE_IMAGE_TEXT -> return message.imageUrl != null
                    && message.text != null
        }
        return false
    }

    override fun onSelectionChanged(count: Int) {
        selectionCount = count
        menu!!.findItem(R.id.action_delete).isVisible = count > 0
        menu!!.findItem(R.id.action_copy).isVisible = count > 0
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chat_actions_menu, menu)
        this.menu = menu
        onSelectionChanged(0)
        return true
    }

    override fun onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed()
        } else {
            mAdapter!!.unselectAllItems()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> mAdapter!!.deleteSelectedMessages()
            R.id.action_copy -> {
                mAdapter!!.copySelectedMessagesText(this, messageStringFormatter, true)
                toast(getString(R.string.copied_message))
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }

    private val messageStringFormatter: MessagesListAdapter.Formatter<MyHistoryMessage>
        get() = MessagesListAdapter.Formatter { message ->
            val createdAt = SimpleDateFormat(Constants.TIMEFORMAT, Locale.getDefault())
                    .format(message.createdAt)

            var text: String? = message.text
            if (text == null) text = "[attachment]"

            String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.user!!.name, text, createdAt)
        }

    companion object {
        val ARG_CHAT_HISTORY = "mChatHistory"
        fun open(con: Context, chatHistory: ChatHistory){
            con.startActivity(Intent(con, HistoryList::class.java).putExtra(ARG_CHAT_HISTORY,chatHistory))
        }
    }

}
