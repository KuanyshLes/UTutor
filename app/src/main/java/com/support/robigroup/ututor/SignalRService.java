package com.support.robigroup.ututor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Credentials;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.Request;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler3;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;

import static com.support.robigroup.ututor.commons.ExtensionsKt.logd;

public class SignalRService extends Service {

    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private Handler mHandler; // to display Toast message
    private final IBinder mBinder = new LocalBinder(); // Binder given to clients

    public SignalRService() {
        logd("starting service in constructor");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
        return result;
    }

    @Override
    public void onDestroy() {
        mHubConnection.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        startSignalR();
        return mBinder;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

    /**
     * method for clients (activities)
     */
    public void sendMessage(String message) {
        String SERVER_METHOD_SEND = "Send";
        mHubProxy.invoke(SERVER_METHOD_SEND, message);
    }

    /**
     * method for clients (activities)
     */
    public void sendMessage_To(String receiverName, String message) {
        String SERVER_METHOD_SEND_TO = "SendChatMessage";
        mHubProxy.invoke(SERVER_METHOD_SEND_TO, receiverName, message);
    }

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        String serverUrl = "http://ututor.azurewebsites.net/";
        mHubConnection = new HubConnection(serverUrl);
        String SERVER_HUB_CHAT = "chat";
        mHubConnection.setCredentials(new Credentials() {
            @Override
            public void prepareRequest(Request request) {
                request.addHeader("Authorization","bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiZXliaXQ5MkBnbWFpbC5jb20iLCJqdGkiOiJhNGU0ZGZhOS1hMGYyLTQ0ZmQtYWYxZC05OWUwZTk3MTVjYzIiLCJpYXQiOjE1MDI0NTQyNDgsIm5iZiI6MTUwMjQ1NDI0OCwiZXhwIjoxNTAzMDU5MDQ4LCJpc3MiOiJVVHV0b3JJc3N1ZXIiLCJhdWQiOiJVVHV0b3JBdWRpZW5jZSJ9.d3fy-Gf-ISXeF40hEjc_q7X4_4ji6Msxjm1VwJvejpo");
            }
        });
        mHubProxy = mHubConnection.createHubProxy(SERVER_HUB_CHAT);
        ClientTransport clientTransport = new ServerSentEventsTransport(mHubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = mHubConnection.start(clientTransport);

        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("SimpleSignalR", e.toString());
            return;
        }

        sendMessage("Hello from BNK!");

        String CLIENT_METHOD_BROADAST_MESSAGE = "lessonChatReceived";
        mHubProxy.on(CLIENT_METHOD_BROADAST_MESSAGE,
                new SubscriptionHandler3<Long,String,String>() {
                    @Override
                    public void run(Long aLong, String time, String message) {
                        logd("chat "+aLong.toString()+" "+time+" "+message);
                    }
                },Long.class,String.class,String.class);
    }
}
