package com.support.robigroup.ututor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.RequestListen
import com.support.robigroup.ututor.screen.chat.model.CustomMessage
import com.support.robigroup.ututor.screen.chat.model.MyMessage
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.realm.Realm
import microsoft.aspnet.signalr.client.ConnectionState
import microsoft.aspnet.signalr.client.Logger
import microsoft.aspnet.signalr.client.Platform
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import microsoft.aspnet.signalr.client.transport.LongPollingTransport
import java.util.concurrent.ExecutionException
import kotlin.properties.Delegates

class SignalRService : Service() {
    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mHubConnection: HubConnection? = null
    private var mHubProxy: HubProxy? = null
    private val TOKEN = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    private val SERVER_URL = Constants.BASE_URL
    private val SERVER_HUB_CHAT = "chat"
    private val TAG_SIGNALR = "signalR"
    private var realm: Realm by Delegates.notNull()

    //chat responses
    private val CLIENT_METHOD_BROADAST_MESSAGE = "lessonChatReceived"
    private val TEACHER_ACCEPTED = "TeacherAccepted"
    private val CHAT_READY= "ChatReady"


    var currentConnectionState: ConnectionState = ConnectionState.Disconnected

    override fun onCreate() {
        super.onCreate()
        realm = Realm.getDefaultInstance()
        if(mHubConnection==null||mHubConnection!!.state==ConnectionState.Disconnected){
            startSignalR()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        return result
    }

    override fun onDestroy() {
//        mHubConnection?.stop()
        realm.close()
        super.onDestroy()
    }

    fun sendMessage(message: String) {
        val SERVER_METHOD_SEND = "Send"
        mHubProxy!!.invoke(SERVER_METHOD_SEND, message)
    }

    fun sendMessage_To(receiverName: String, message: String) {
        val SERVER_METHOD_SEND_TO = "SendChatMessage"
        mHubProxy!!.invoke(SERVER_METHOD_SEND_TO, receiverName, message)
    }

    private fun startSignalR() {
        Platform.loadPlatformComponent(AndroidPlatformComponent())

        val neededToken = TOKEN.replace("bearer ","")
        val logger = Logger { message, level -> logd(level.toString() +":  " + message,TAG_SIGNALR+"LogLevel") }

        mHubConnection = HubConnection("http://ututor.azurewebsites.net","authorization="+neededToken,true, logger)

        mHubProxy = mHubConnection!!.createHubProxy(SERVER_HUB_CHAT)

        mHubConnection!!.closed {
            logd("closed",TAG_SIGNALR)
            startSignalR()
        }
        connectSignalR()
    }

    fun connectSignalR(){

        mHubProxy!!.subscribe(object : Any() {
            @SuppressWarnings("unused")
            fun TeacherAccepted(message: String){
                logd(message,TAG_SIGNALR)
                notifyTeacherAccepted(message)
            }
            @SuppressWarnings("unused")
            fun ChatReady(){
                notifyesChatReadyFromTeacher()
            }
            @SuppressWarnings("unused")
            fun lessonChatReceived(message: CustomMessage){
                logd(Gson().toJson(message, CustomMessage::class.java))
                notifyMessageReceived(message)
            }
        })

        val awaitConnection = mHubConnection!!.start(LongPollingTransport(mHubConnection!!.logger))
        try {
            awaitConnection.get()
        } catch (e: InterruptedException) {
            Log.e("onErrorOccured",e.toString())
        } catch (e: ExecutionException) {
            Log.e("onErrorOccured",e.toString())


        }

        mHubConnection!!.reconnecting {
            mHubConnection!!.stop()
        }

        mHubConnection!!.received( { json ->
            Log.e("onMessageReceived ", json.toString())
        })
    }

    private fun notifyTeacherAccepted(message: String){
        val realm = Realm.getDefaultInstance()
        val request = realm.where(RequestListen::class.java).findFirst()
        realm.executeTransaction {
            request.status = Constants.STATUS_ACCEPTED
        }
    }

    private fun notifyesChatReadyFromTeacher(){
        val realm = Realm.getDefaultInstance()
        val request = realm.where(RequestListen::class.java).findFirst()
        realm.executeTransaction {
            request.status = Constants.STATUS_TEACHER_CONFIRMED
        }
    }

    private fun notifyMessageReceived(message: CustomMessage){
        Realm.getDefaultInstance().executeTransaction {
            val realmMessage = Realm.getDefaultInstance().where(CustomMessage::class.java).findFirst()
            realmMessage.Id = message.Id
            realmMessage.File = message.File
            realmMessage.FileThumbnail = message.FileThumbnail
            realmMessage.Message = message.Message
            realmMessage.Time = message.Time
        }
    }
}

