package com.support.robigroup.ututor

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.CustomMessage
import com.support.robigroup.ututor.model.content.RequestListen
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.realm.Realm
import microsoft.aspnet.signalr.client.*
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport
import java.net.URLEncoder
import java.util.concurrent.ExecutionException
import kotlin.properties.Delegates

class SignalRService : IntentService("SignalRService") {


    private var mHubConnection: HubConnection? = null
    private var mHubProxy: HubProxy? = null
    private val mBinder = LocalBinder()
    private val TOKEN = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    private val SERVER_URL = Constants.BASE_URL
    private val SERVER_HUB_CHAT = "chat"
    private val CLIENT_METHOD_BROADAST_MESSAGE = "lessonChatReceived"
    private val TAG_SIGNALR = "signalR"
    private var realm: Realm by Delegates.notNull()

    var currentConnectionState: ConnectionState = ConnectionState.Disconnected

    private fun startSignalR() {
        Platform.loadPlatformComponent(AndroidPlatformComponent())

        val neededToken = TOKEN.replace("bearer ","")
        logd(TOKEN+"\n"+neededToken,tag = TAG_SIGNALR)

        mHubConnection = HubConnection(SERVER_URL+"signalr","authorization="+neededToken,false, object : Logger {
            override fun log(message: String, level: LogLevel) {
                logd(message)
            }
        })

        mHubProxy = mHubConnection!!.createHubProxy(SERVER_HUB_CHAT)
        mHubConnection!!.stateChanged { fromState, toState ->
            logd("${fromState.name} -> ${toState.name}",TAG_SIGNALR)
            currentConnectionState = toState
        }
        mHubConnection!!.closed {
            logd("closed",TAG_SIGNALR)
            connectSignalR(mHubConnection)
        }
        connectSignalR(mHubConnection)


        sendMessage("Hello from BNK!")
        mHubProxy!!.on(CLIENT_METHOD_BROADAST_MESSAGE,
                { custom -> logd("chat " + Gson().toJson(custom,CustomMessage::class.java)) }, CustomMessage::class.java)
        mHubProxy!!.subscribe(this)
    }

    override fun onCreate() {
        super.onCreate()
        realm = Realm.getDefaultInstance()
        startSignalR()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        logd("onStartCommand",TAG_SIGNALR)
        return result
    }

    override fun onHandleIntent(p0: Intent?) {
        logd("onHandleIntent",TAG_SIGNALR)


    }

    override fun onDestroy() {
        mHubConnection?.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    inner class LocalBinder : Binder() {
        // Return this instance of SignalRService so clients can call public methods
        val service: SignalRService
            get() = this@SignalRService
    }

    /**
     * method for clients (activities)
     */
    fun sendMessage(message: String) {
        val SERVER_METHOD_SEND = "Send"
        mHubProxy!!.invoke(SERVER_METHOD_SEND, message)
    }

    /**
     * method for clients (activities)
     */
    fun sendMessage_To(receiverName: String, message: String) {
        val SERVER_METHOD_SEND_TO = "SendChatMessage"
        mHubProxy!!.invoke(SERVER_METHOD_SEND_TO, receiverName, message)
    }

    fun connectSignalR(connection: HubConnection?){
        val clientTransport = ServerSentEventsTransport(mHubConnection!!.logger)
        val signalRFuture = mHubConnection!!.start(clientTransport)
        try {
            signalRFuture.get()
        } catch (e: InterruptedException) {
            Log.e(TAG_SIGNALR, e.toString())
            return
        } catch (e: ExecutionException) {
            Log.e(TAG_SIGNALR, e.toString())
            return
        }
    }

    private fun notifyTeacherAccepted(){
        val realm = Realm.getDefaultInstance()
        val request = realm.where(RequestListen::class.java).findFirst()
        realm.executeTransaction {
            request.status = 1
        }
    }


}
