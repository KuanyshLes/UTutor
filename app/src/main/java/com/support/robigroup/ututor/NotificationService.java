package com.support.robigroup.ututor;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.support.robigroup.ututor.commons.ChatInformation;
import com.support.robigroup.ututor.commons.ChatLesson;
import com.support.robigroup.ututor.commons.Functions;
import com.support.robigroup.ututor.ui.chat.model.ChatMessage;
import com.support.robigroup.ututor.singleton.SingletonSharedPref;

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

    public static boolean isStarted = false;

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
            Log.i("Notification Service",s);
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
        Log.e("Notification Service", "on create service");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHubConnection.stop();
        realm.close();
        isStarted = false;
        Log.e("Notification Service", "on destroy service");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mHubConnection==null||mHubConnection.getState()== ConnectionState.Disconnected){
            startSignalR();
            isStarted = true;
        }
        return START_STICKY;
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
                                    Log.e("Notification Service","TeacherAccepted: "+(new Gson()).toJson(chatInformation,ChatInformation.class));
                                    notifyTeacherAccepted(chatInformation);
                                }
                            });
                        }
                    }
                    , ChatInformation.class);

            mHubProxy.on("ChatMemberReady",
                    new SubscriptionHandler() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("Notification Service","ChatReady");
                                    notifyChatReady();
                                }
                            });
                        }
                    });

            mHubProxy.on("lessonChatReceived",
                    new SubscriptionHandler1<ChatMessage>() {
                        @Override
                        public void run(final ChatMessage msg) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("Notification Service", (new Gson()).toJson(msg,ChatMessage.class));
                                    notifyMessageReceived(msg);
                                }
                            });
                        }
                    }
                    , ChatMessage.class);
            mHubProxy.on("ChatCompleted",
                    new SubscriptionHandler1<ChatLesson>(){
                        @Override
                        public void run(final ChatLesson lesson) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("Notification Service","ChatCompleted");
                                    notifyChatCompleted(lesson);
                                }
                            });
                        }
                    }
                    , ChatLesson.class
            );
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e("Notification Service","onTaskRemoved");
        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent =PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);
    }

    private void notifyChatCompleted(final ChatLesson chatLesson) {
        if(realm.isClosed())
            realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ChatInformation request = realm.where(ChatInformation.class).findFirst();
                if(request!=null){
                    request.setStatusId(Constants.INSTANCE.getSTATUS_COMPLETED());
                    request.setInvoiceSum(chatLesson.getInvoiceSum());
                    request.setEndTime(chatLesson.getEndTime());
                    request.setDuration(chatLesson.getDuration());
                    request.setInvoiceTariff(chatLesson.getInvoiceTariff());
                }
            }
        });
        stopSelf();
    }

    private void notifyTeacherAccepted(final ChatInformation chatInformation){
        if(realm.isClosed())
            realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.where(ChatInformation.class).findAll().deleteAllFromRealm();
                chatInformation.setDeviceCreateTime(Functions.INSTANCE.getDeviceTime());
                realm.copyToRealm(chatInformation);
            }
        });
    }

    private void notifyChatReady(){
        if(realm.isClosed())
            realm = Realm.getDefaultInstance();
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

    private void notifyMessageReceived(final ChatMessage message){
        if(realm.isClosed())
            realm = Realm.getDefaultInstance();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.copyToRealm(message);
            }
        });
    }
}
