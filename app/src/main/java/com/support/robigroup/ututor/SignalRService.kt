package com.support.robigroup.ututor

import android.app.IntentService
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import microsoft.aspnet.signalr.client.ConnectionState
import microsoft.aspnet.signalr.client.Credentials
import microsoft.aspnet.signalr.client.Platform
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import java.util.concurrent.ExecutionException

class SignalRService : IntentService("SignalRService") {


    private var mHubConnection: HubConnection? = null
    private var mHubProxy: HubProxy? = null
    private val mBinder = LocalBinder()
    private val TOKEN = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    private val SERVER_URL = Constants.BASE_URL
    private val SERVER_HUB_CHAT = "chat"
    private val CLIENT_METHOD_BROADAST_MESSAGE = "lessonChatReceived"
    private val TAG_SIGNALR = "signalR"

    var currentConnectionState: ConnectionState = ConnectionState.Disconnected

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        logd("onStartCommand",TAG_SIGNALR)
        return result
    }

    override fun onHandleIntent(p0: Intent?) {
        logd("onHandleIntent",TAG_SIGNALR)
        if(currentConnectionState != ConnectionState.Connected){
            startSignalR()
        }
    }

    override fun onDestroy() {
        mHubConnection!!.stop()
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

    private fun startSignalR() {
        Platform.loadPlatformComponent(AndroidPlatformComponent())

        mHubConnection = HubConnection(SERVER_URL)
        mHubConnection!!.credentials = Credentials { request -> request.addHeader("Authorization",TOKEN) }
        mHubProxy = mHubConnection!!.createHubProxy(SERVER_HUB_CHAT)

        mHubConnection!!.stateChanged { fromState, toState ->
            logd("${fromState.name} -> ${toState.name}",TAG_SIGNALR)
            currentConnectionState = toState
        }

        mHubConnection!!.closed {
            logd("closed",TAG_SIGNALR)
//            connectSignalR(mHubConnection)
        }

        connectSignalR(mHubConnection)

//        val clientTransport = ServerSentEventsTransport(mHubConnection!!.logger)
//        val signalRFuture = mHubConnection!!.start(clientTransport)
//        try {
//            signalRFuture.get()
//        } catch (e: InterruptedException) {
//            Log.e("SimpleSignalR", e.toString())
//            return
//        } catch (e: ExecutionException) {
//            Log.e("SimpleSignalR", e.toString())
//            return
//        }

        sendMessage("Hello from BNK!")

        mHubProxy!!.on(CLIENT_METHOD_BROADAST_MESSAGE,
                { aLong, time, message -> logd("chat " + aLong!!.toString() + " " + time + " " + message) }, Long::class.java, String::class.java, String::class.java)
    }

    fun connectSignalR(connection: HubConnection?){
        try {
            connection!!.start().get()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        }

    }
}
