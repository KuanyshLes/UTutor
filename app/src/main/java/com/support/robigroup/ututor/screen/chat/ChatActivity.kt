package com.support.robigroup.ututor.screen.chat

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatActivity
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
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.SignalRService
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.AppUtils
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.model.content.RequestListen
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.screen.chat.custom.media.holders.CustomIncomingMessageViewHolder
import com.support.robigroup.ututor.screen.chat.custom.media.holders.CustomOutcomingMessageViewHolder
import com.support.robigroup.ututor.screen.chat.model.CustomMessage
import com.support.robigroup.ututor.screen.chat.model.MyMessage
import com.support.robigroup.ututor.screen.chat.model.User
import com.support.robigroup.ututor.screen.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates


class ChatActivity : AppCompatActivity(),
        MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageHolders.ContentChecker<MyMessage>,
        DialogInterface.OnClickListener,
        FinishDialog.NoticeDialogListener,
        MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener,
        ContentManager.PickContentListener{


    private var messagesList: MessagesList? = null
    private var messagesAdapter: MessagesListAdapter<MyMessage>? = null
    private val subscriptions: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private var contentManager: ContentManager? = null

    private var user: User = User("mukhtar","Mukhtar",null,true)
    private var teacher: User by Delegates.notNull()
    private var menu: Menu? = null
    private var selectionCount: Int = 0
    private var lastLoadedDate: Date? = null
    private var realm: Realm by Delegates.notNull()
    private var realmChangeListener: RealmChangeListener<CustomMessage> by Delegates.notNull()
    private var imageLoader: ImageLoader? = null
    private val ex_teacher = "{\"Birthday\":\"0001-01-01T00:00:00\",\"Classes\":\"1,10\",\"FirstName\":\"Aktore\",\"Id\":\"8ce5ddc5-46cf-4306-b994-af004b09729e\",\"Languages\":\"kk-KZ,ru-KZ\",\"LastName\":\"Niyazymbetov\",\"Raiting\":0.0}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        contentManager = ContentManager(this,this)

        val teacher: Teacher = intent.getParcelableExtra<Teacher>(KEY_TEACHER) as Teacher
//        val teacher: Teacher = Gson().fromJson(ex_teacher,Teacher::class.java)
        this.teacher = User(teacher.Id,teacher.FirstName,teacher.Image,true)

        setSupportActionBar(toolbar)
        teacher_name_title.text =this.teacher.name

        findViewById<View>(R.id.text_finish).setOnClickListener { showFinishDialog() }

        val intent = Intent()
        intent.setClass(this, SignalRService::class.java)
        startService(intent)

        imageLoader = ImageLoader { imageView, url -> Picasso.with(baseContext).load(url).into(imageView) }

        messagesList = findViewById(R.id.messagesList)
        initAdapter()

        val input = findViewById<MessageInput>(R.id.input)
        input.setInputListener(this)
        input.setAttachmentsListener(this)
    }

    override fun onRestart() {
        super.onRestart()
        setOnRealmChangeListeners()
    }

    private fun setOnRealmChangeListeners(){
        realm = Realm.getDefaultInstance()
        var result = realm.where(CustomMessage::class.java).findFirst()
        realm.executeTransaction {
            if(result == null){
                result = realm.createObject(CustomMessage::class.java)
            }
        }
        realmChangeListener = RealmChangeListener {
            message ->
            if(message.File!=null&&message.FileThumbnail!=null){
                val myMessage = CustomMessage(message.Id,message.Time,Constants.BASE_URL+message.FileThumbnail,
                        Constants.BASE_URL+message.File,message.Message)
                logd(Gson().toJson(myMessage,CustomMessage::class.java),"mymessage2")
                messagesAdapter?.addToStart(MyMessage(myMessage,teacher),true)
            }else{
                val myMessage = CustomMessage(message.Id,message.Time,Message = message.Message)
                logd(Gson().toJson(myMessage,CustomMessage::class.java),"mymessage2")
                messagesAdapter?.addToStart(MyMessage(myMessage,teacher),true)
            }
        }
        result?.addChangeListener(realmChangeListener)

        var closeListener = realm.where(RequestListen::class.java).findFirst()
        if(closeListener==null)
            realm.executeTransaction{
                closeListener = realm.createObject(RequestListen::class.java,0)
            }
        closeListener?.addChangeListener(
                RealmChangeListener<RequestListen>{
                    rs ->
                    if(rs?.status==Constants.STATUS_COMPLETED) {
                        closeChat()
                    }
                })
    }

    override fun onStart() {
        super.onStart()
        setOnRealmChangeListeners()
    }

    override fun onStop() {
        subscriptions.clear()
        realm.where(CustomMessage::class.java).findFirst()?.removeChangeListener(realmChangeListener)
        realm.close()
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
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


        messagesAdapter = MessagesListAdapter(user.id, holders,imageLoader)
        messagesAdapter!!.enableSelectionMode(this)
//        messagesAdapter!!.setLoadMoreListener(this)
        messagesList!!.setAdapter(messagesAdapter)
    }

    override fun onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed()
        } else {
            messagesAdapter!!.unselectAllItems()
        }
    }

    override fun onLoadMore(page: Int, totalItemsCount: Int) {
//        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
//            loadMessages()
//        }
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
                AppUtils.showToast(this, R.string.copied_message, true)
            }
        }
        return true
    }

    private val messageStringFormatter: MessagesListAdapter.Formatter<MyMessage>
        get() = MessagesListAdapter.Formatter { message ->
            val createdAt = SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                    .format(message.createdAt)

            var text: String? = message.text
            if (text == null) text = "[attachment]"

            String.format(Locale.getDefault(), "%s: %s (%s)",
                    message.User!!.name, text, createdAt)
        }

    private fun showFinishDialog() {
        val dialogFragment = FinishDialog()
        dialogFragment.show(supportFragmentManager, "finishDialog")
    }

    override fun onSubmit(input: CharSequence): Boolean {
        sendMessage(input)
        return true
    }

    private fun sendMessage(input: CharSequence){
        val subscription = MainManager().sendMessage(messageText = input.toString())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { message ->
                            if(requestErrorHandler(message.code(),message.message())){
                                val myMessage: MyMessage? = MyMessage(message.body()!!,user)
                                messagesAdapter!!.addToStart(myMessage, true)
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
                                startActivity(Intent(this@ChatActivity, MainActivity::class.java))
                                finish()
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

    private fun sendFileMessage(imageUri: String){
        val encodedImage = Functions.getEncodedImage(imageUri)

        val subscription = MainManager().sendMessage(file64base = encodedImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { messageResponse ->
                            if(requestErrorHandler(messageResponse.code(),messageResponse.message())){
                                val message: CustomMessage = messageResponse.body()!!
                                if(message.File!=null&&message.FileThumbnail!=null){
                                    val myMessage = CustomMessage(message.Id,message.Time,Constants.BASE_URL+message.FileThumbnail,
                                            Constants.BASE_URL+message.File,message.Message)
                                    messagesAdapter?.addToStart(MyMessage(myMessage,teacher),true)
                                }else{
                                    val myMessage = CustomMessage(message.Id,message.Time,Message = message.Message)
                                    messagesAdapter?.addToStart(MyMessage(myMessage,teacher),true)
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

//    protected fun loadMessages() {
//        Handler().postDelayed(//imitation of internet connection
//        {
//            val messages = MessagesFixtures.getMessages(lastLoadedDate)
//            lastLoadedDate = messages[messages.size - 1].createdAt
//            messagesAdapter!!.addToEnd(messages, false)
//        }, 1000)
//    }

    override fun onAddAttachments() {
        AlertDialog.Builder(this)
                .setItems(R.array.view_types_dialog, this)
                .show()
    }

    override fun hasContentFor(message: MyMessage, type: Byte): Boolean {
        when (type) {
            CONTENT_TYPE_IMAGE_TEXT -> return message.imageUrl != null
                    && message.text != null
        }
        return false
    }

    override fun onClick(dialogInterface: DialogInterface, i: Int) {
        when (i) {
            0 ->
//                messagesAdapter!!.addToStart(MyMessage(CustomMessage(3218498,"00:35","https://vlast.kz/media/pages/mt/1481870380x9dmr_1000x768.jpg","","https://vlast.kz/media/pages/mt/1481870380x9dmr_1000x768.jpg"),user), true)
                contentManager?.pickContent(ContentManager.Content.IMAGE)
//            1 -> messagesAdapter!!.addToStart(MessagesFixtures.getVoiceMessage(), true)
        }
    }


    override fun onDialogPositiveClick(dialog: DialogFragment) {
        dialog.dismiss()
        closeChat()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        contentManager?.onRestoreInstanceState(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        contentManager?.onSaveInstanceState(outState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        //Need for handle result
        contentManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStartContentLoading() {

    }

    override fun onContentLoaded(uri: Uri?, contentType: String?) {
        if (contentType.equals(ContentManager.Content.IMAGE.toString())) {
            //You can use any library for display image Fresco, Picasso, ImageLoader
            //For sample:
            if(uri!=null){
                messagesAdapter!!.addToStart(MyMessage(CustomMessage(3218498,"00:35",uri.toString(),uri.toString(),"https://vlast.kz/media/pages/mt/1481870380x9dmr_1000x768.jpg"),user), true)
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

    companion object {

        private val CONTENT_TYPE_IMAGE_TEXT: Byte = 1
        private val KEY_TEACHER = "teacher"
        private val TOTAL_MESSAGES_COUNT = 100


        fun open(context: Context, teacher: Teacher) {
            context.startActivity(Intent(context, ChatActivity::class.java).putExtra(KEY_TEACHER, teacher))
        }
    }


}
