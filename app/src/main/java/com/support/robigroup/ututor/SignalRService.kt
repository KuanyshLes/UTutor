package com.support.robigroup.ututor

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.os.RemoteException
import android.util.Log

import com.support.robigroup.ututor.singleton.SingletonSharedPref

import java.util.concurrent.ExecutionException

import microsoft.aspnet.signalr.client.Credentials
import microsoft.aspnet.signalr.client.Platform
import microsoft.aspnet.signalr.client.SignalRFuture
import microsoft.aspnet.signalr.client.http.Request
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3
import microsoft.aspnet.signalr.client.transport.ClientTransport
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport

import com.support.robigroup.ututor.commons.logd

class SignalRService : Service() {

    private var mHubConnection: HubConnection? = null
    private var mHubProxy: HubProxy? = null
    private val mBinder = LocalBinder()
    private val TOKEN = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    private val SERVER_URL = Constants.BASE_URL
    private val SERVER_HUB_CHAT = "chat"
    private val CLIENT_METHOD_BROADAST_MESSAGE = "lessonChatReceived"





    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val result = super.onStartCommand(intent, flags, startId)
        startSignalR()

        return result
    }

    override fun onDestroy() {

        mHubConnection!!.stop()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        // Return the communication channel to the service.
        startSignalR()
        return mBinder
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
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

        val clientTransport = ServerSentEventsTransport(mHubConnection!!.logger)
        val signalRFuture = mHubConnection!!.start(clientTransport)

        try {
            signalRFuture.get()
        } catch (e: InterruptedException) {
            Log.e("SimpleSignalR", e.toString())
            return
        } catch (e: ExecutionException) {
            Log.e("SimpleSignalR", e.toString())
            return
        }

        sendMessage("Hello from BNK!")

        mHubProxy!!.on(CLIENT_METHOD_BROADAST_MESSAGE,
                { aLong, time, message -> logd("chat " + aLong!!.toString() + " " + time + " " + message) }, Long::class.java, String::class.java, String::class.java)
    }
}
