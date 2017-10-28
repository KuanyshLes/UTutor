package com.support.robigroup.ututor.features.chat

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.gson.Gson
import com.squareup.picasso.Picasso
import com.stfalcon.chatkit.commons.ImageLoader
import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesList
import com.stfalcon.chatkit.messages.MessagesListAdapter
import com.stfalcon.contentmanager.ContentManager
import com.stfalcon.frescoimageviewer.ImageViewer
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.features.chat.custom.media.holders.CustomIncomingMessageViewHolder
import com.support.robigroup.ututor.features.chat.custom.media.holders.CustomOutcomingMessageViewHolder
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.features.chat.model.ChatUser
import com.support.robigroup.ututor.features.main.MenuActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.Realm
import io.realm.RealmObjectChangeListener
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class ChatActivity : AppCompatActivity(),
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageHolders.ContentChecker<ChatMessage>,
        DialogInterface.OnClickListener,
        OnChatActivityDialogInteractionListener,
        MessagesListAdapter.SelectionListener,
        ContentManager.PickContentListener{


    private var messagesList: MessagesList? = null
    private var messagesAdapter: MessagesListAdapter<ChatMessage>? = null
    private var mReadyDialog: ReadyDialog by Delegates.notNull()
    private val subscriptions: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private var contentManager: ContentManager? = null

    private var user: ChatUser by Delegates.notNull()
    private var teacher: ChatUser by Delegates.notNull()
    private var mChatInformation: ChatInformation by Delegates.notNull()
    private var menu: Menu? = null
    private var selectionCount: Int = 0
    private var realm: Realm by Delegates.notNull()
    private var imageLoader: ImageLoader? = null
    private var mMessages: RealmResults<ChatMessage> by Delegates.notNull()

    private var mChatChangeListener: RealmObjectChangeListener<ChatInformation> = RealmObjectChangeListener {
        rs, changeset ->
        logd(rs.toString()+changeset.toString())
        if(changeset!=null&&!changeset.isDeleted){
            if(rs.StatusId == Constants.STATUS_COMPLETED){
                runOnUiThread {
                    OnEvalChat(Functions.getUnmanagedChatInfo(rs))
                }
            }else if(rs.TeacherReady&&rs.LearnerReady){
                mReadyDialog.dismiss()
            }}else if(rs.LearnerReady&&!rs.TeacherReady){
            mReadyDialog.onLearnerReady()
        }
    }

    private val mMessagesChangeListener: OrderedRealmCollectionChangeListener<RealmResults<ChatMessage>>
            = OrderedRealmCollectionChangeListener { messages, changeSet ->
        if (changeSet == null) {
            notifyItemRangeInserted(0,messages.size-1)
        }else{
            // For deletions, the adapter has to be notified in reverse order.
            val deletions = changeSet.deletionRanges
            for (i in deletions.size-1 downTo 0) {
                val range = deletions[i]
                notifyItemRangeRemoved(range.startIndex, range.length)
            }

            val insertions = changeSet.insertionRanges
            for (range in insertions) {
                notifyItemRangeInserted(range.startIndex, range.length)
            }

            val modifications = changeSet.changeRanges
            for (range in modifications) {
                notifyItemRangeChanged(range.startIndex, range.length)
            }
        }
    }

    private fun notifyItemRangeChanged(startIndex: Int,rangeLength: Int){
    }
    private fun notifyItemRangeInserted(startIndex: Int,rangeLength: Int){
        for(i in startIndex until startIndex+rangeLength){
            messagesAdapter?.addToStart(mMessages[i],true)
        }
    }
    private fun notifyItemRangeRemoved(startIndex: Int,rangeLength: Int){
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        contentManager = ContentManager(this,this)

        realm = Realm.getDefaultInstance()
        mMessages = realm.where(ChatMessage::class.java).findAll()
        mMessages.addChangeListener(mMessagesChangeListener)


        mChatInformation = realm.where(ChatInformation::class.java).findFirst()!!
        mChatInformation.addChangeListener(mChatChangeListener)

        teacher = ChatUser(Constants.TEACHER_ID, mChatInformation.Teacher,null,true)
        user = ChatUser(Constants.LEARNER_ID, mChatInformation.Learner,null,true)

        setSupportActionBar(toolbar)
        teacher_name_title.text =this.teacher.name!!.split(" ")[0]

        findViewById<View>(R.id.text_finish).setOnClickListener { showFinishDialog() }

        val intent = Intent()
        intent.setClass(this, NotificationService::class.java)
        startService(intent)

        imageLoader = ImageLoader { imageView, url -> Picasso.with(baseContext).load(url).into(imageView) }

        messagesList = findViewById(R.id.messagesList)
        initAdapter()
        if(messagesAdapter!!.isEmpty)
            reloadMessages()

        val input = findViewById<MessageInput>(R.id.input)
        input.setInputListener(this)
        input.setAttachmentsListener(this)


        mReadyDialog = ReadyDialog()
        if(!mChatInformation.TeacherReady||!mChatInformation.LearnerReady){
            mReadyDialog.isCancelable = false
            val dif = Functions.getDifferenceInMillis(mChatInformation.CreateTime)
            val utc = dif - 6*60*60*1000
            logd(utc.toString())
            if((dif>1000&&dif<Constants.WAIT_TIME)||(utc>1000&&utc<Constants.WAIT_TIME)){
                mReadyDialog.startShow(supportFragmentManager,TAG_READY_DIALOG,dif)
            }else{
                finishCorrectly()
            }
        }else if(mReadyDialog.isVisible){
            mReadyDialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
        if(mReadyDialog.isVisible)
            mReadyDialog.dismiss()
        if(mMessages.isValid)
            mMessages.removeAllChangeListeners()
        if(mChatInformation.isValid)
            mChatInformation.removeAllChangeListeners()
        realm.close()

    }

    private fun initAdapter() {
        val holders = MessageHolders()
                .registerContentType(
                        CONTENT_TYPE_IMAGE_TEXT,
                        CustomIncomingMessageViewHolder::class.java,
                        R.layout.item_incoming_text_image_message,
                        CustomOutcomingMessageViewHolder::class.java,
                        R.layout.item_outcoming_text_image_message,
                        this)

        messagesAdapter = MessagesListAdapter(user.id, holders, imageLoader)
        messagesAdapter!!.enableSelectionMode(this)
        messagesAdapter!!.setOnMessageClickListener {
            message: ChatMessage ->
            if(message.filePath!=null)
                ImageViewer.Builder(this, arrayOf(message.imageUrl))
                        .setStartPosition(0)
                        .show()
        }
        messagesList!!.setAdapter(messagesAdapter)
    }

    private val messageStringFormatter: MessagesListAdapter.Formatter<ChatMessage>
        get() = MessagesListAdapter.Formatter { message ->
            val createdAt = SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(message.createdAt)

            var text: String? = message.text
            if (text == null) text = "[attachment]"

            String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.user!!.name, text, createdAt)
        }

    private fun showFinishDialog() {
        val builder = android.support.v7.app.AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.prompt_are_you_sure))
                .setCancelable(false)
                .setPositiveButton("OK")
                {
                    dialog, id ->
                    dialog.cancel()
                    closeChat()
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

    private fun postLearnerReady(){
        val subscription = MainManager().postLearnerReady()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(requestErrorHandler(teachers.code(),teachers.message())){
                                val res = teachers.body()?.charStream()?.readText()
                                realm.executeTransaction {
                                    if(res != null && res.equals("ready")){
                                        mChatInformation.LearnerReady = true
                                        mChatInformation.TeacherReady = true
                                    }else if(res != null){
                                        mChatInformation.LearnerReady = true
                                    }
                                }
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }

    private fun sendMessage(input: CharSequence){
        val subscription = MainManager().sendMessage(messageText = input.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { message ->
                            if(requestErrorHandler(message.code(),message.message())){
                                realm.executeTransaction {
                                    realm.copyToRealm(message.body())
                                }
                            }else{
                                //TODO handle http errors
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }

    private fun closeChat(){
        val subscription = MainManager().postChatComplete()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { message ->
                            if(requestErrorHandler(message.code(),message.message())){
                                OnEvalChat(Functions.getChatInformation(message.body()!!))
                            }else{
                                finishCorrectly()
                            }
                        },
                        { e ->
                            Log.e("Error",e.message)
                            finishCorrectly()
                        }
                )
        subscriptions.add(subscription)
    }

    private fun reloadMessages() {

        messagesAdapter?.addToEnd(mMessages.toList(), true)
    }

    private fun evalChat(rating: Int,lessonId:Int){
        val subscription = MainManager().evalChat(rating,lessonId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { message ->
                            if(requestErrorHandler(message.code(),message.message())){
                                finishCorrectly()
                            }else{
                                finishCorrectly()
                            }
                        },
                        { e ->
                            Log.e("Error",e.stackTrace.toString())
                            finishCorrectly()

                        }
                )
        subscriptions.add(subscription)
    }

    private fun OnEvalChat(chatLesson: ChatInformation){
        val fin = FinishDialog()
        fin.showMe(chatLesson,supportFragmentManager,"finishDial")
    }

    private fun sendFileMessage(imageUri: String){
        val encodedImage = Functions.getEncodedImage(imageUri)
        if(encodedImage!=null){
            val subscription = MainManager().sendMessage(file64base = encodedImage)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            { messageResponse ->
                                val message: ChatMessage? = messageResponse.body()
                                if(requestErrorHandler(messageResponse.code(),messageResponse.message())&&message!=null){
                                    realm.executeTransaction {
                                        realm.copyToRealm(message)
                                    }
                                }else{
                                    //TODO handle http errors
                                }
                            },
                            { e ->
                                logd(e.message ?: "error")
                                Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                            }
                    )
            subscriptions.add(subscription)
        }

    }

    override fun onFinishCounterFromReadyDialog() {
        if(!mChatInformation.LearnerReady||!mChatInformation.TeacherReady){
            finishCorrectly()

        }
    }

    override fun onEvaluateDialogPositiveClick(rating: Float) {
        evalChat((rating*20).toInt(),mChatInformation.Id!!)

    }
    override fun onCancelEvalDialog() {
        finishCorrectly()

    }

    override fun onReadyDialogReadyClick(dialog: DialogFragment) {
        if(!mChatInformation.LearnerReady)
            postLearnerReady()
    }


    //interface methods for cutting and else
    override fun onStartContentLoading() {

    }

    override fun onContentLoaded(uri: Uri?, contentType: String?) {
        if (contentType.equals(ContentManager.Content.IMAGE.toString())) {
            //You can use any library for display image Fresco, Picasso, ImageLoader
            //For sample:
            if(uri!=null){
                sendFileMessage(uri.path)
            }
        }
    }

    override fun onCanceled() {
    }

    override fun onError(error: String?) {
        Snackbar.make(findViewById(android.R.id.content), error ?: "", Snackbar.LENGTH_LONG).show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        contentManager?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed()
        } else {
            messagesAdapter!!.unselectAllItems()
        }
    }

    private fun finishCorrectly(){
        realm.executeTransaction {
            mMessages.deleteAllFromRealm()
            if(mChatInformation.isValid){
                mChatInformation.deleteFromRealm()
            }
        }
        startActivity(Intent(this@ChatActivity, MenuActivity::class.java))
        finish()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_delete -> messagesAdapter!!.deleteSelectedMessages()
            R.id.action_copy -> {
                messagesAdapter!!.copySelectedMessagesText(this, messageStringFormatter, true)
                toast(getString(R.string.copied_message))
            }
        }
        return true
    }

    override fun onAddAttachments() {
        AlertDialog.Builder(this)
                .setItems(R.array.view_types_dialog, this)
                .show()
    }

    override fun hasContentFor(message: ChatMessage, type: Byte): Boolean {
        when (type) {
            CONTENT_TYPE_IMAGE_TEXT -> {
                return message.filePath != null && message.fileIconPath !=null
            }
        }
        return false
    }

    override fun onSubmit(input: CharSequence): Boolean {
        sendMessage(input)
        return true
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        when (i) {
            0 ->
                contentManager?.pickContent(ContentManager.Content.IMAGE)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        contentManager?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentManager?.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //Need for handle result
        contentManager?.onActivityResult(requestCode, resultCode, data)
    }

    companion object {

        val CONTENT_TYPE_IMAGE_TEXT: Byte = 100
        private val KEY_TEACHER = "teacher"
        private val TOTAL_MESSAGES_COUNT = 100
        private val TAG_READY_DIALOG = "readyDialog"


        fun open(context: Context) {
            val intent = Intent(context, ChatActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

}
