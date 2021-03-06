package com.support.robigroup.ututor.ui.chat

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewPropertyAnimator
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.dewarder.holdinglibrary.HoldingButtonLayout
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.contentmanager.ContentManager
import com.stfalcon.frescoimageviewer.ImageViewer
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.GlideApp
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.base.CircleProgressBarDrawable
import com.support.robigroup.ututor.ui.chat.eval.RateDialog
import com.support.robigroup.ututor.ui.chat.holders.IncomingAudioMessageVH
import com.support.robigroup.ututor.ui.chat.holders.IncomingImageMessageVH
import com.support.robigroup.ututor.ui.chat.holders.OutcomingAudioMessageVH
import com.support.robigroup.ututor.ui.chat.holders.OutcomingImageMessageVH
import com.support.robigroup.ututor.ui.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.chat.ready.ReadyDialog
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerActivity
import com.support.robigroup.ututor.utils.CommonUtils
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

class ActivityChat : BaseActivity(), ChatMvpView {

    private lateinit var messagesList: MessagesList
    private lateinit var messagesAdapter: MessagesListAdapter<ChatMessage>
    private lateinit var contentManager: ContentManager
    private lateinit var menu: Menu
    private var selectionCount: Int = 0
    private val imageLoader = ImageLoader { imageView, url -> GlideApp.with(baseContext).load(url).fitCenter().into(imageView) }
    private val messageStringFormatter = MessagesListAdapter.Formatter<ChatMessage> { message ->
        val createdAt = SimpleDateFormat(Constants.DEVICE_TIMEFORMAT, Locale.getDefault()).format(message.createdAt)
        var text: String? = message.text
        if (text == null) text = "[attachment]"
        String.format(Locale.getDefault(), "%s: %s (%s)",
                message.user!!.name, text, createdAt)
    }

    @Inject
    lateinit var mPresenter: ChatMvpPresenter<ChatMvpView>

    //audio parts
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var mediaRecorder: MediaRecorder
    private lateinit var mRecordFilePath: String
    //holding button
    private val SLIDE_TO_CANCEL_ALPHA_MULTIPLIER = 2.5f
    private val TIME_INVALIDATION_FREQUENCY = 50L
    private var mTimerRunnable: Runnable? = null
    private val mFormatter = SimpleDateFormat("mm:ss:SS")
    private var mStartTime: Long = 0

    private lateinit var mHoldingButtonLayout: HoldingButtonLayout
    private lateinit var mTime: TextView
    private lateinit var mInput: EditText
    private lateinit var mSlideToCancel: View
    private lateinit var sendTextMessageIV: ImageView
    private lateinit var attachFileIV: ImageView
    private lateinit var startRecordIV: ImageView

    private var mAnimationDuration: Int = 0
    var mTimeAnimator: ViewPropertyAnimator? = null
    var mSlideToCancelAnimator: ViewPropertyAnimator? = null
    var mInputAnimator: ViewPropertyAnimator? = null

    private val permissions = arrayOf(Manifest.permission.RECORD_AUDIO)
    private val REQUEST_RECORD_AUDIO_PERMISSION = 3

    lateinit var vibrator: Vibrator


    //activity lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        setUp()
        mPresenter.onViewInitialized()
        if(!NotificationService.isStarted){
            val intent = Intent()
            intent.setClass(this, NotificationService::class.java)
            startService(intent)
        }
    }

    override fun onFragmentDetached(tag: String?) {
        if (tag != null) {
            when (tag) {
                Constants.TAG_RATE_DIALOG ->
                    startDrawerActivity()
            }
        }
        super.onFragmentDetached(tag)
    }

    override fun showImage(url: String) {
        val circleProgressBar = CircleProgressBarDrawable()
        circleProgressBar.barWidth = CommonUtils.getPixelsFromDPs(this, 2)
        val hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(resources)
                .setRetryImage(R.drawable.retry_image)
                .setProgressBarImage(circleProgressBar)
        ImageViewer.Builder(this, arrayOf(url))
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder)
                .setStartPosition(0)
                .show()
    }

    override fun onDestroy() {
        if(mediaPlayer.isPlaying)
            mediaPlayer.stop()
        mediaPlayer.release()
        mPresenter.onDetach()
        super.onDestroy()
    }

    override fun setUp() {
        setSupportActionBar(drawer_toolbar)
        //holding button
        mInput = findViewById(R.id.input)
        mInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.isNotEmpty()) {
                    showSendTexMessageBtn(true)
                } else if (charSequence.isEmpty()) {
                    showSendTexMessageBtn(false)
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })

        mHoldingButtonLayout = findViewById(R.id.input_holder)
        if(hasPermission(permissions[0])){
            mHoldingButtonLayout.addListener(mPresenter)
            logd("permission added")
        }else{
            requestPermissionsSafely(permissions, REQUEST_RECORD_AUDIO_PERMISSION)
        }

        mTime = findViewById(R.id.time)
        mSlideToCancel = findViewById(R.id.slide_to_cancel)

        mAnimationDuration = resources.getInteger(android.R.integer.config_shortAnimTime)

        sendTextMessageIV = findViewById(R.id.sendTextMessage)
        sendTextMessageIV.setOnClickListener {
            showSendTexMessageBtn(false)
            val text = mInput.text.trim().toString()
            mPresenter.onSubmit(text)
            mInput.text.clear()
        }

        attachFileIV = findViewById(R.id.attachIV)
        attachFileIV.setOnClickListener {
            AlertDialog.Builder(this)
                    .setItems(R.array.view_types_dialog, (
                            DialogInterface.OnClickListener { p0, order ->
                                when (order) {
                                    0 -> contentManager.pickContent(ContentManager.Content.IMAGE)
                                }
                            })
                    ).show()

        }

        startRecordIV = findViewById(R.id.start_record)

        //messages list
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

        contentManager = ContentManager(this, mPresenter)
        text_finish.setOnClickListener { mPresenter.onFinishClick() }

        //audio initialising
        mediaPlayer = MediaPlayer()
        mediaPlayer.setOnCompletionListener(mPresenter)
        mSlideToCancel = findViewById(R.id.slide_to_cancel)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> messagesAdapter.deleteSelectedMessages()
            R.id.action_copy -> {
                messagesAdapter.copySelectedMessagesText(this, messageStringFormatter, true)
                showMessage(R.string.copied_message)
            }
        }
        return true
    }

    override fun onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed()
        } else {
            messagesAdapter.unselectAllItems()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        contentManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        contentManager.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentManager.onSaveInstanceState(outState)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_RECORD_AUDIO_PERMISSION ->
                if (grantResults.isNotEmpty()
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mHoldingButtonLayout.addListener(mPresenter)
                    logd("permission added")
                } else {
                    val builder = AlertDialog.Builder(this)
                    builder.setMessage(getString(R.string.prompt_audio_message_warning))
                            .setCancelable(false)
                            .setPositiveButton("Да")
                            { dialog, id ->
                                dialog.cancel()
                                requestPermissionsSafely(permissions, REQUEST_RECORD_AUDIO_PERMISSION)
                            }
                            .setNegativeButton("Нет не надо")
                            { dialog, id ->
                                dialog.cancel()
                            }
                    val alert = builder.create()
                    alert.setCancelable(false)
                    alert.show()
                }
        }

        contentManager.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    //PlayView
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

    //RecordView
    override fun startRecord(filePath: String) {
        mRecordFilePath = filePath
        mediaRecorder = MediaRecorder()
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        mediaRecorder.setAudioEncodingBitRate(20000)
        mediaRecorder.setAudioSamplingRate(16000)
        mediaRecorder.setOutputFile(filePath)
        mediaRecorder.prepare()
        mediaRecorder.start()
    }

    override fun stopRecord() {
        try {
            mediaRecorder.stop()
        }finally {
            mediaRecorder.release()
        }
    }

    override fun getFilePath(): String = mRecordFilePath

    //HoldingButtonView
    override fun showCancelled() {
        vibrator.vibrate(500)
        showMessage(R.string.error_cancelled)
    }

    override fun showSubmitted() {
        showMessage(R.string.audio_sent)
    }

    override fun cancelAnimations() {
        mInputAnimator?.cancel()
        mSlideToCancelAnimator?.cancel()
        mTimeAnimator?.cancel()
    }

    override fun startCollapseAnimations() {
        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(0f).setDuration(mAnimationDuration.toLong())
        mSlideToCancelAnimator?.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mSlideToCancel.visibility = View.INVISIBLE
                mSlideToCancelAnimator?.setListener(null)
            }
        })
        mSlideToCancelAnimator?.start()

        mInput.alpha = 0f
        mInput.visibility = View.VISIBLE
        mInputAnimator = mInput.animate().alpha(1f).setDuration(mAnimationDuration.toLong())
        mInputAnimator?.start()

        mTimeAnimator = mTime.animate().translationY(mTime.height.toFloat()).alpha(0f).setDuration(mAnimationDuration.toLong())
        mTimeAnimator?.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mTime.visibility = View.INVISIBLE
                mTimeAnimator?.setListener(null)
            }
        })
        mTimeAnimator?.start()
    }

    override fun startExpandAnimations() {
        mSlideToCancel.translationX = 0f
        mSlideToCancel.alpha = 0f
        mSlideToCancel.visibility = View.VISIBLE
        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(1f).setDuration(mAnimationDuration.toLong())
        mSlideToCancelAnimator?.start()

        mInputAnimator = mInput.animate().alpha(0f).setDuration(mAnimationDuration.toLong())
        mInputAnimator?.setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                mInput.visibility = View.INVISIBLE
                mInputAnimator?.setListener(null)
            }
        })
        mInputAnimator?.start()

        mTime.translationY = mTime.height.toFloat()
        mTime.alpha = 0f
        mTime.visibility = View.VISIBLE
        mTimeAnimator = mTime.animate().translationY(0f).alpha(1f).setDuration(mAnimationDuration.toLong())
        mTimeAnimator?.start()
    }

    override fun moveSlideToCancel(offset: Float, isCancel: Boolean) {
        mSlideToCancel.translationX = -mHoldingButtonLayout.width * offset
        mSlideToCancel.alpha = 1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * offset
    }

    override fun startTimer() {
        mStartTime = System.currentTimeMillis()
        repeadTimer()
    }

    override fun getStartTime(): Long = mStartTime

    private fun repeadTimer() {
        mTimerRunnable = Runnable {
            mTime.text = getFormattedTime()
            repeadTimer()
        }
        mTime.postDelayed(mTimerRunnable, TIME_INVALIDATION_FREQUENCY)
    }

    override fun stopTimer() {
        if (mTimerRunnable != null) {
            mTime.handler.removeCallbacks(mTimerRunnable)
        }
    }

    private fun getFormattedTime(): String {
        return mFormatter.format(Date(System.currentTimeMillis() - mStartTime))
    }

    private fun showSendTexMessageBtn(show: Boolean) {
        if (show) {
            mHoldingButtonLayout.isButtonEnabled = false
            startRecordIV.visibility = View.GONE
            sendTextMessageIV.visibility = View.VISIBLE
        } else {
            mHoldingButtonLayout.isButtonEnabled = true
            sendTextMessageIV.visibility = View.GONE
            startRecordIV.visibility = View.VISIBLE
        }
    }

    //dialog, activity methods
    override fun startDrawerActivity() {
        mPresenter.onChatFinished()
        DrawerActivity.open(this)
        finish()
    }

    override fun showFinishDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.prompt_are_you_sure))
                .setCancelable(false)
                .setPositiveButton("OK")
                { dialog, id ->
                    dialog.cancel()
                    mPresenter.onOkFinishClick()
                }
                .setNegativeButton("Cancel")
                { dialog, id ->
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

    override fun notifyItemRangeInserted(messages: List<ChatMessage>, startIndex: Int, rangeLength: Int) {
        for (i in startIndex until startIndex + rangeLength) {
            messagesAdapter.addToStart(messages[i], true)
        }
    }

    override fun notifyItemRangeUpdated(messages: List<ChatMessage>, startIndex: Int, rangeLength: Int) {

    }

    override fun notifyItemRangeDeleted(messages: List<ChatMessage>, startIndex: Int, rangeLength: Int) {
        for (i in startIndex until startIndex + rangeLength) {
            messagesAdapter.deleteById(messages[i].id)
        }
    }

    companion object {
        fun open(context: Context) {
            val intent = Intent(context, ActivityChat::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }


}
