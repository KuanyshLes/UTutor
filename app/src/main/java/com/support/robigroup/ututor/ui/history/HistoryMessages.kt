package com.support.robigroup.ututor.ui.history

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.facebook.drawee.drawable.ProgressBarDrawable
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.frescoimageviewer.ImageViewer
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.GlideApp
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.ChatHistory
import com.support.robigroup.ututor.commons.toast
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.base.CircleProgressBarDrawable
import com.support.robigroup.ututor.ui.chat.PlayPresenter
import com.support.robigroup.ututor.ui.chat.holders.IncomingAudioMessageVH
import com.support.robigroup.ututor.ui.chat.holders.IncomingImageMessageVH
import com.support.robigroup.ututor.ui.chat.holders.OutcomingAudioMessageVH
import com.support.robigroup.ututor.ui.chat.holders.OutcomingImageMessageVH
import com.support.robigroup.ututor.ui.chat.model.ChatMessage
import com.support.robigroup.ututor.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_history_messages.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject


class HistoryMessages : BaseActivity(), HistoryMvpView{


    @Inject
    lateinit var mPresenter: HistoryMvpPresenter<HistoryMvpView>

    private lateinit var messagesList: MessagesList
    private lateinit var messagesAdapter: MessagesListAdapter<ChatMessage>

    private lateinit var menu: Menu
    private var selectionCount: Int = 0
    private val imageLoader = ImageLoader { imageView, url -> GlideApp.with(baseContext).load(url).fitCenter().into(imageView) }

    private val messageStringFormatter: MessagesListAdapter.Formatter<ChatMessage>
        get() = MessagesListAdapter.Formatter { message ->
            val createdAt = SimpleDateFormat(Constants.DEVICE_TIMEFORMAT, Locale.getDefault())
                    .format(message.createdAt)

            var text: String? = message.text
            if (text == null) text = "[attachment]"

            String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.user!!.name, text, createdAt)
        }

    private lateinit var mediaPlayer: MediaPlayer



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_messages)

        activityComponent.inject(this)
        mPresenter.onAttach(this)

        setUp()
        mPresenter.onViewInitialized()
    }

    override fun setToolbarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun getChatHistory(): ChatHistory {
        return intent.getParcelableExtra(ARG_CHAT_HISTORY)
    }

    override fun setUp() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        messagesList = findViewById(R.id.messagesList)
        val holders = MessageHolders()
                .registerContentType(
                        Constants.CONTENT_TYPE_IMAGE_TEXT,
                        IncomingImageMessageVH::class.java,
                        R.layout.item_incoming_text_image_message,
                        OutcomingImageMessageVH::class.java,
                        R.layout.item_outcoming_text_image_message,
                        mPresenter)
                .registerContentType(
                        Constants.CONTENT_TYPE_VOICE,
                        IncomingAudioMessageVH::class.java,
                        R.layout.item_incoming_audio_message,
                        OutcomingAudioMessageVH::class.java,
                        R.layout.item_outcoming_audio_message,
                        mPresenter)

        messagesAdapter = MessagesListAdapter(Constants.LEARNER_ID, holders, imageLoader)
        messagesAdapter.enableSelectionMode({ count ->
            selectionCount = count
            menu.findItem(R.id.action_delete).isVisible = false
            menu.findItem(R.id.action_copy).isVisible = count > 0
        })
        messagesAdapter.setOnMessageClickListener(mPresenter)
        messagesList.setAdapter(messagesAdapter)

        //audio initialising
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener(mPresenter)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.chat_actions_menu, menu)
        this.menu = menu
        messagesAdapter.unselectAllItems()
        return true
    }

    override fun onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed()
        } else {
            messagesAdapter.unselectAllItems()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> messagesAdapter.deleteSelectedMessages()
            R.id.action_copy -> {
                messagesAdapter.copySelectedMessagesText(this, messageStringFormatter, true)
                toast(getString(R.string.copied_message))
            }
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if(mediaPlayer.isPlaying)
            mediaPlayer.stop()
        mediaPlayer.release()
        mPresenter.onDetach()
    }

    override fun showImage(url: String) {
        val circleProgressBar = CircleProgressBarDrawable()
        circleProgressBar.barWidth = CommonUtils.getPixelsFromDPs(this, 2)
        circleProgressBar.color = ContextCompat.getColor(this, R.color.colorLightBlue)
        val hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(resources)
                .setRetryImage(R.drawable.retry_image)
                .setProgressBarImage(circleProgressBar)
        ImageViewer.Builder(this, arrayOf(url))
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                .setStartPosition(0)
                .show()

    }

    override fun addMessages(messages: List<ChatMessage>) {
        messagesAdapter.clear()
        messagesAdapter.addToEnd(messages, false)
    }

    //play view
    @SuppressLint("NewApi")
    override fun preparePlay(filePath: String) {
        mediaPlayer.reset()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes =
                    AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
            mediaPlayer.setAudioAttributes(audioAttributes)
        }else{
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        mediaPlayer.setDataSource(filePath)
        mediaPlayer.prepare()
        mPresenter.onPlayerPrepared()
    }

    override fun getPlayDuration(): Int {
        return mediaPlayer.duration
    }

    override fun startPlay() {
        mediaPlayer.start()
    }

    override fun stopPlay() {
        if(mediaPlayer.isPlaying)
            mediaPlayer.stop()
    }

    override fun getCurrentPlayingTime(): Int {
        return mediaPlayer.currentPosition
    }

    override fun pausePlay() {
        mediaPlayer.pause()
    }

    override fun getPlayPresenter(): PlayPresenter = mPresenter

    override fun seekTo(progress: Int) {
        mediaPlayer.seekTo(progress)
    }

    companion object {
        val ARG_CHAT_HISTORY = "mChatHistory"
        fun open(con: Context, chatHistory: ChatHistory){
            con.startActivity(Intent(con, HistoryMessages::class.java).putExtra(ARG_CHAT_HISTORY,chatHistory))
        }
    }

}
