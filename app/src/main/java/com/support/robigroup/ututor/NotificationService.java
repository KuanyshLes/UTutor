package com.support.robigroup.ututor;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.support.robigroup.ututor.commons.Functions;
import com.support.robigroup.ututor.model.content.ChatInformation;
import com.support.robigroup.ututor.model.content.ChatLesson;
import com.support.robigroup.ututor.screen.chat.model.CustomMessage;
import com.support.robigroup.ututor.singleton.SingletonSharedPref;

import java.util.List;

import io.realm.Realm;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.LogLevel;
import microsoft.aspnet.signalr.client.Logger;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.LongPollingTransport;


public class NotificationService extends Service {

    private static final String CHAT_HUB = "chat";
    private static final String SERVER_URL = "http://ututor.kz";
    private static final String KEY_TOKEN = "TOKEN";

    private final static String MESSAGE_RECEIVED = "lessonChatReceived";
    private final static String TEACHER_ACCEPTED = "TeacherAccepted";
    private final static String CHAT_READY= "ChatReady";
    private final static String CHAT_COMPLETED= "ChatReady";

    private HubConnection mHubConnection;
    private HubProxy mHubProxy;
    private String token;
    private Realm realm;
    private Handler handler;
    private final Logger logger = new Logger() {
        @Override
        public void log(String s, LogLevel logLevel) {
            Log.d("MyLogger",s);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        realm = Realm.getDefaultInstance();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHubConnection.stop();
        realm.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mHubConnection==null||mHubConnection.getState()== ConnectionState.Disconnected)
            startSignalR();
        return super.onStartCommand(intent, flags, startId);
    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    private void startSignalR() {
        String token = SingletonSharedPref.getInstance().getString(KEY_TOKEN,"bearer ").replace("bearer ","");
        String queryString = "authorization="+token;
        if(token.equals("")){

        }else{
            Platform.loadPlatformComponent(new AndroidPlatformComponent());
            mHubConnection = new HubConnection(SERVER_URL,queryString,true, logger);
            mHubProxy = mHubConnection.createHubProxy(CHAT_HUB);

            mHubConnection.reconnecting(new Runnable() {
                @Override
                public void run() {
                    mHubConnection.stop();
                }
            });

            mHubConnection.closed(
                    new Runnable() {
                        @Override
                        public void run() {
                            if(Functions.INSTANCE.isOnline(getBaseContext()))
                                mHubConnection.start(new LongPollingTransport(logger));
                        }
                    }
            );

            mHubConnection.start(new LongPollingTransport(logger));

            mHubProxy.on("TeacherAccepted",
                    new SubscriptionHandler1<ChatInformation>() {
                        @Override
                        public void run(final ChatInformation chatInformation) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("MyEvent","TeacherAccepted: "+(new Gson()).toJson(chatInformation,ChatInformation.class));
                                    notifyTeacherAccepted(chatInformation);
                                }
                            });
                        }
                    }
                    , ChatInformation.class);

            mHubProxy.on("ChatReady",
                    new SubscriptionHandler() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("MyEvent","ChatReady");
                                    notifyChatReady();
                                }
                            });
                        }
                    });

            mHubProxy.on("lessonChatReceived",
                    new SubscriptionHandler1<CustomMessage>() {
                        @Override
                        public void run(final CustomMessage msg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("MyEventMessage", (new Gson()).toJson(msg,CustomMessage.class));
                                    notifyMessageReceived(msg);
                                }
                            });
                        }
                    }
                    , CustomMessage.class);
            mHubProxy.subscribe(
                    new CallbacksFromServer(){
                        @Override
                        public void ChatCompleted(final List<ChatLesson> lessons) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("MyEvent","ChatCompleted");
                                    notifyChatCompleted(lessons.get(0));
                                }
                            });
                        }
                    }
            );
//            mHubProxy.on("ChatCompleted",
//                    new SubscriptionHandler1<ChatLesson>() {
//                        @Override
//                        public void run(final ChatLesson msg) {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.e("MyEvent","ChatCompleted");
//                                    notifyChatCompleted(msg);
//                                }
//                            });
//                        }
//                    }, ChatLesson.class);
        }
    }

    private void notifyChatCompleted(ChatLesson chatLesson) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatInformation request = realm.where(ChatInformation.class).findFirst();
                if(request!=null){
                    request.setStatusId(Constants.INSTANCE.getSTATUS_COMPLETED());
                }
            }
        });
    }

    private void notifyTeacherAccepted(final ChatInformation chatInformation){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(ChatInformation.class).findAll().deleteAllFromRealm();
                realm.copyToRealm(chatInformation);
            }
        });
    }

    private void notifyChatReady(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatInformation request = realm.where(ChatInformation.class).findFirst();
                if(request!=null){
                    request.setTeacherReady(true);
                }
            }
        });
    }

    private void notifyMessageReceived(final CustomMessage message){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(message);
            }
        });
    }

    private interface CallbacksFromServer{
        void ChatCompleted(List<ChatLesson> lessons);
    }

}
