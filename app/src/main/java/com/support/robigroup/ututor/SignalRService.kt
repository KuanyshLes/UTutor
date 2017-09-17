package com.support.robigroup.ututor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.ChatInformation
import com.support.robigroup.ututor.screen.chat.model.CustomMessage
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.realm.Realm
import microsoft.aspnet.signalr.client.Action
import microsoft.aspnet.signalr.client.ConnectionState
import microsoft.aspnet.signalr.client.Logger
import microsoft.aspnet.signalr.client.Platform
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import java.util.concurrent.ExecutionException
import kotlin.properties.Delegates

class SignalRService : Service() {
    override fun onBind(p0: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private var mHubConnection: HubConnection? = null
    private var mHubProxy: HubProxy? = null
    private val SERVER_URL = Constants.BASE_URL
    private val SERVER_HUB_CHAT = "chat"
    private val TAG_SIGNALR = "signalR"
    private var realm: Realm by Delegates.notNull()

    //chat responses
    private val CLIENT_METHOD_BROADAST_MESSAGE = "lessonChatReceived"
    private val TEACHER_ACCEPTED = "TeacherAccepted"
    private val CHAT_READY= "ChatReady"

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

    private fun startSignalR() {
        Platform.loadPlatformComponent(AndroidPlatformComponent())

        val neededToken = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN).replace("bearer ","")
        val logger = Logger { message, level -> logd(level.toString() +":  " + message,TAG_SIGNALR+"LogLevel") }

        mHubConnection = HubConnection("http://ututor.kz","authorization="+neededToken,true, logger)

        mHubProxy = mHubConnection!!.createHubProxy(SERVER_HUB_CHAT)

        mHubProxy!!.subscribe(object : Any() {
            fun TeacherAccepted(message: Int){
                Log.e("Event", "TeacherAccepted: "+message)
                notifyTeacherAccepted(message)
            }
            fun ChatReady(){
                Log.e("Event", "ChatReady")
                notifyesChatReadyFromTeacher()
            }
            fun lessonChatReceived(message: CustomMessage){
                Log.e("Event","lessonChatReceived: "+Gson().toJson(message,CustomMessage::class.java))
                notifyMessageReceived(message)
            }
            fun ChatCompleted(){
                Log.e("Event","ChatCompleted")
                notifyChatCompleted()
            }
        })

        mHubConnection!!.closed {
            Log.e("Event","Connection Closed")
            if(Functions.isOnline(baseContext))
                startSignalR()
        }
        connectSignalR()

//        mHubConnection?.reconnecting {
//            mHubConnection?.stop()
//        }
    }

    fun connectSignalR(){

        val awaitConnection = mHubConnection!!.start()
        try {
            awaitConnection.done{
                Log.e("Event","Done")
            }
        } catch (e: InterruptedException) {
            Log.e("EventError",e.toString())
        } catch (e: ExecutionException) {
            Log.e("EventError",e.toString())
        }
    }

    private fun notifyChatCompleted() {
        val realm = Realm.getDefaultInstance()
        val request = realm.where(ChatInformation::class.java).findFirst()
        realm.executeTransaction {
            request?.StatusId = Constants.STATUS_COMPLETED
        }
        realm.close()
    }

    private fun notifyTeacherAccepted(message: Int){
        val realm = Realm.getDefaultInstance()
        val request = realm.where(ChatInformation::class.java).findFirst()
        realm.executeTransaction {
            request?.StatusId = Constants.STATUS_ACCEPTED_TEACHER
            request?.Id = message
        }
        realm.close()

    }

    private fun notifyesChatReadyFromTeacher(){
        val realm = Realm.getDefaultInstance()
        val request = realm.where(ChatInformation::class.java).findFirst()
        realm.executeTransaction {
            request?.TeacherReady = true
        }
    }

    private fun notifyMessageReceived(message: CustomMessage){
        Realm.getDefaultInstance().executeTransaction {
            val realmMessage = Realm.getDefaultInstance().where(CustomMessage::class.java).findFirst()
            realmMessage?.Id = message.Id
            realmMessage?.File = message.File
            realmMessage?.FileThumbnail = message.FileThumbnail
            realmMessage?.Message = message.Message
            realmMessage?.Time = message.Time
        }
    }
}

