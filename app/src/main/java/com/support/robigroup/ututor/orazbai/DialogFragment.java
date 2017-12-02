package com.support.robigroup.ututor.orazbai;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Vibrator;
import android.view.ViewPropertyAnimator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dewarder.holdinglibrary.HoldingButtonLayout;
import com.dewarder.holdinglibrary.HoldingButtonLayoutListener;
import com.example.root.blablachat.MainActivity;
import com.example.root.blablachat.R;
import com.example.root.blablachat.SocketIO.MySocket;
import com.example.root.blablachat.SocketIO.constants.MessageStates;
import com.example.root.blablachat.User;
import com.example.root.blablachat.custom.holders.message.dialogs.audio.CustomIncomingDialogAudioMessageViewHolder;
import com.example.root.blablachat.custom.holders.message.dialogs.audio.AudioPlayerCallback;
import com.example.root.blablachat.custom.holders.message.dialogs.image.CustomIncomingDialogImageMessageViewHolder;
import com.example.root.blablachat.custom.holders.message.dialogs.image.CustomOutcomingDialogImageMessageViewHolder;
import com.example.root.blablachat.custom.holders.message.dialogs.audio.CustomOutcomingDialogAudioMessageViewHolder;
import com.example.root.blablachat.custom.holders.message.dialogs.text.UnreadDialogMessage;
import com.example.root.blablachat.custom.holders.message.dialogs.text.CustomIncomingDialogTextMessageViewHolder;
import com.example.root.blablachat.custom.holders.message.dialogs.text.CustomOutcomingDialogTextMessageViewHolder;
import com.example.root.blablachat.data.DialogMessages;
import com.example.root.blablachat.data.PrivateRooms;
import com.example.root.blablachat.fragments.imagesilder.ImageSliderFragment;
import com.example.root.blablachat.models.dialog.DialogRoom;
import com.example.root.blablachat.models.dialog.DialogMessageNew;
import com.example.root.blablachat.models.MimeType;
import com.example.root.blablachat.utils.BroadcastConstants;
import com.example.root.blablachat.utils.Notifier;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.Base64;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;
import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by root on 07.08.17.
 */

public class DialogFragment extends Fragment implements MessageHolders.ContentChecker<DialogMessageNew>,HoldingButtonLayoutListener {
    String logTAG = DialogFragment.class.getName();

    MainActivity context;

    BroadcastReceiver broadcastReceiver;


    DialogRoom privateRoom;

    private boolean typing=false;

    AlertDialog dialog;

    public static final int REQUEST_CODE_CAMERA_PERMISSION=1234;

    Vibrator vibrator;





    //UI
    View view;
    Toolbar toolbar;
    ImageView sendTextMessageIV,attachFileIV,startRecordIV;
    SwipeRefreshLayout swipeRefreshLayout;
    MessagesList messagesList;
    LinearLayout inputCont;

    RelativeLayout holdLongerLayout;

//    MessageInput messageInput;


    SwipeRefreshLayout.OnRefreshListener onRefreshListener;
    MessagesListAdapter<DialogMessageNew> adapter;

    AudioPlayerCallback currentsCallback=null;


    //new message input components
    private static final DateFormat mFormatter = new SimpleDateFormat("mm:ss:SS");
    private static final float SLIDE_TO_CANCEL_ALPHA_MULTIPLIER = 2.5f;
    private static final long TIME_INVALIDATION_FREQUENCY = 50L;

    private HoldingButtonLayout mHoldingButtonLayout;
    private TextView mTime;
    private EditText mInput;
    private View mSlideToCancel;

    private int mAnimationDuration;
    private ViewPropertyAnimator mTimeAnimator;
    private ViewPropertyAnimator mHoldLongerAnimator;
    private ViewPropertyAnimator mSlideToCancelAnimator;
    private ViewPropertyAnimator mInputAnimator;

    private long mStartTime;
    private Runnable mTimerRunnable;


    ///media record
    String AudioSavePathInDevice = null;
    MediaRecorder mediaRecorder ;
    Random random ;
    String RandomAudioFileName = "ABCDEFGHIJKLMNOP";

    public MediaPlayer mediaPlayer;

    Handler mHandler=new Handler();

    ArrayList <String> images;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(logTAG, "OnCreate");
        context=(MainActivity)getActivity();

        vibrator=(Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);

        Bundle bundle = getArguments();
        String room_id = bundle.getString("room_id");
        String room_name=bundle.getString("room_name");
        privateRoom= new DialogRoom(room_id, room_name, "https://s-media-cache-ak0.pinimg.com/736x/53/05/1f/53051f60f26e826ca2368cfc4234d73e--leo-messi-messi-.jpg", false);

        random = new Random();



    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(logTAG, "OnCreateView");
//        setHasOptionsMenu(true);
        view = inflater.inflate(R.layout.dialog_fragment, container, false);



        setupToolbar();

        setHasOptionsMenu(true);


        bindViews();

        createAlertDialog();

        context.showBottomNavigationView(false);


        onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showAllMessages();
                swipeRefreshLayout.setRefreshing(false);
            }
        };
        swipeRefreshLayout.setOnRefreshListener(onRefreshListener);

        sendTextMessageIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showSendTexMessageBtn(false);
                typing=false;

                long ca = System.currentTimeMillis();
                String id = context.functions.constructID(User.getInstance().getUsername(), privateRoom.getId(), ca);
                String body = mInput.getText().toString().trim();
                mInput.setText("");

                DialogMessageNew dialogMessageNew=new DialogMessageNew(id,User.getInstance().getUsername(), PrivateRooms.getInterlocutorId(privateRoom.getId()),privateRoom.getId(),true,MimeType.text,body,ca, MessageStates.WAITING);

                DialogMessages.insertNewMessage(dialogMessageNew);
                MySocket.getInstance().sendDialogMessage(dialogMessageNew);

                adapter.addToStart(dialogMessageNew,true);
            }
        });
            attachFileIV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    requestPermissionCamera();
                }
            });

        mInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0&&!typing){
                    typing=true;
                    sendTyping();
                    showSendTexMessageBtn(true);
                }else if (charSequence.length()==0&&typing){
                    typing=false;
                    sendTyping();
                    showSendTexMessageBtn(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        initAdapter();

        onRefreshListener.onRefresh();

        createMediaPlayer();
        createMediaRecorder();


        return view;

    }

    @Override
    public void onStart() {
        setupStatusView();
        super.onStart();
        Log.e(logTAG, "OnStart");
        broadcastReceiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context1, Intent intent) {
                Log.e(logTAG,"onreceive");

                final DialogMessageNew message=(DialogMessageNew)intent.getSerializableExtra(BroadcastConstants.MESSAGE_SERIALIZABLE);

                    switch (intent.getStringExtra(BroadcastConstants.TYPE)) {
                        case BroadcastConstants.NEW_MESSAGE:
                            if(privateRoom.getId().equals(message.getRoom_id())) {
                                if (message.getMime_type().equals(MimeType.image)){
                                    if (images!=null){
                                        images.add(message.getBody());
                                    }
                                }
                                adapter.addToStart(message, true);
                                MySocket.getInstance().sendViewed(message);
                                DialogMessages.setMessageState(message.getId(), MessageStates.READ);
                            }
                            break;
                        case BroadcastConstants.MESSAGE_STATE_CHANGED:

                            if(privateRoom.getId().equals(message.getRoom_id())) {
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {

                                        adapter.update(message.getId(), message);
                                    }
                                },200);
                            }
                            break;
                        case BroadcastConstants.PRESENCE_CHANGED:

                            String username=intent.getStringExtra(User.usernameField);
                            if (PrivateRooms.getInterlocutorId(privateRoom.getId()).equals(username)){
                                setupStatusView();
                            }
                            break;
                        case BroadcastConstants.TYPING:
                            String from_username=intent.getStringExtra("from");
                            String room=intent.getStringExtra("room");
                            boolean isTyping=intent.getBooleanExtra("isTyping",false);

                            Log.e(logTAG,"Typing "+from_username);

                            if (room.equals(privateRoom.getId())){
                                showTyping(isTyping);
                            }


                }
            }
        };
        IntentFilter intentFilter=new IntentFilter(BroadcastConstants.ACTIONDIALOGS);
        getActivity().registerReceiver(broadcastReceiver,intentFilter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(logTAG, "OnResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(logTAG, "OnPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(logTAG, "OnStop");

        stopAudio();

        typing=false;
        sendTyping();

        if(broadcastReceiver!=null){
            getActivity().unregisterReceiver(broadcastReceiver);
            broadcastReceiver=null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        context.updateUnreadDialogMessagesCount();
        context.functions.hideKeyboard();
        Log.e(logTAG, "OnDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(logTAG, "onDestroy");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.dialog_more,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setDialogName:
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean hasContentFor(DialogMessageNew message, byte type) {
        switch (type){
            case DialogMessageNew.CONTENT_TYPE_TEXT:
                return message.getMime_type().equals(MimeType.text);
            case DialogMessageNew.CONTENT_TYPE_IMAGE:
                return message.getMime_type().equals(MimeType.image);
            case DialogMessageNew.CONTENT_TYPE_AUDIO:
                return message.getMime_type().equals(MimeType.audio);
            case DialogMessageNew.CONTENT_TYPE_UNREAD:
                return message.getMime_type().equals(MimeType.unread);
        }
        return false;
    }
    public void setCurrentsCallback(AudioPlayerCallback callback){
        this.currentsCallback=callback;
    }
    public void stopPrevious(){
        if (currentsCallback!=null) {
            currentsCallback.onNewPlay();
        }
    }
    private void showSendTexMessageBtn(boolean show){
        if(show){
            mHoldingButtonLayout.setButtonEnabled(false);
            startRecordIV.setVisibility(View.GONE);
            sendTextMessageIV.setVisibility(View.VISIBLE);
        }else {
            mHoldingButtonLayout.setButtonEnabled(true);
            sendTextMessageIV.setVisibility(View.GONE);
            startRecordIV.setVisibility(View.VISIBLE);
        }
    }

    private void bindViews() {
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swip);
        messagesList = (MessagesList) view.findViewById(R.id.messagesList);

//        messageInput = (MessageInput) view.findViewById(R.id.input);

        mHoldingButtonLayout = (HoldingButtonLayout) view.findViewById(R.id.input_holder);
        mHoldingButtonLayout.addListener(this);

        inputCont=(LinearLayout)view.findViewById(R.id.inputCont);

        sendTextMessageIV=(ImageView)view.findViewById(R.id.sendTextMessage);
        attachFileIV=(ImageView)view.findViewById(R.id.attachIV);
        startRecordIV=(ImageView)view.findViewById(R.id.start_record);

        mTime = (TextView) view.findViewById(R.id.time);
        mInput = (EditText) view.findViewById(R.id.input);
        mSlideToCancel = view.findViewById(R.id.slide_to_cancel);

        holdLongerLayout=(RelativeLayout)view.findViewById(R.id.holdLongerLayout);

        mAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);

    }
    private void showTyping(boolean isActive){
        if(isActive){
            toolbar.setSubtitle("typing");
        }else {
            setupStatusView();
        }
    }

    private void setupToolbar() {
        toolbar=(Toolbar)view.findViewById(R.id.toolbar_native);
        context.setSupportActionBar(toolbar);
        ActionBar actionBar=context.getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(privateRoom.getName());
            actionBar.setSubtitle(R.string.offline);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    void showAllMessages() {
        Log.e(logTAG,"showAllMessages "+privateRoom.getId());
        adapter.clear();
        adapter.notifyDataSetChanged();

        boolean scroll=true;
        int scrollToCount=0;
        Cursor cursor = DialogMessages.getAllMessagesFrom(privateRoom.getId());
        if (cursor.moveToFirst()) {
            images=new ArrayList<>();
            do {
                DialogMessageNew message= DialogMessages.getDialogMessageFromCursor(cursor);
                if (message.getMime_type().equals(MimeType.image)){
                    images.add(message.getBody());
                }
                if (!message.getIsMine()&&message.getState()!=MessageStates.READ){
                    if (scroll){
                        Log.e(logTAG,"place to be added");
                        scroll=false;
                        DialogMessageNew unread=new DialogMessageNew("","","","",false,"","",message.getTimestamp(),0);
                        unread.setMime_type(MimeType.unread);
                        adapter.addToStart(unread,scroll);
                    }
                    Notifier.getInstance(context).notificationManager.cancel(message.getId().hashCode());
                    DialogMessages.setMessageState(message.getId(),MessageStates.READ);
                    MySocket.getInstance().sendViewed(message);
                }
                if(!scroll){
                    scrollToCount++;
                }

                adapter.addToStart(message,scroll);
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.e(logTAG+"scrollTo",String .valueOf(scrollToCount));
        messagesList.smoothScrollToPosition(scrollToCount);


    }
    public void showAlert() {
        Log.e(logTAG,"showAlert");
        final CharSequence[] items = {"Камера", "Галерея",
                "Отмена"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выберите действие");
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Камера")) {
                    EZPhotoPickConfig config = new EZPhotoPickConfig();
                    config.photoSource = PhotoSource.CAMERA;
                    config.exportingSize = 1000;
                    EZPhotoPick.startPhotoPickActivity(context, config);
                } else if (items[item].equals("Галерея")) {

                    EZPhotoPickConfig config = new EZPhotoPickConfig();
                    config.photoSource = PhotoSource.GALLERY;
                    config.needToExportThumbnail = true;
                    config.exportingThumbSize = 200;
                    config.exportingSize = 1000;
                    EZPhotoPick.startPhotoPickActivity(context, config);
                } else if (items[item].equals("Отмена")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();
    }
    private void chooseAudioType(final JSONArray soundTypes, final String originalUrl){
        Log.e(logTAG,"showAlert");
        final int originalIndex=soundTypes.length();
        final CharSequence[] items =new CharSequence[soundTypes.length()+1];
        try {
            for (int i=0;i<soundTypes.length();i++){
                    items[i]=soundTypes.getJSONObject(i).getString("title");

            }
            items[originalIndex]="Без эффекта";
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Выберите эффект голоса");
        builder.setSingleChoiceItems(items,originalIndex, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                try {
                    String url;
                    if (item != originalIndex) {
                        url = soundTypes.getJSONObject(item).getString("url");;
                    } else {
                        url = originalUrl;
                    }
                    playAudio(url);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
        builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListView listView=((AlertDialog)dialogInterface).getListView();
                int chP=listView.getCheckedItemPosition();
                try {
                    String url;
                    if (chP!=originalIndex){
                        url= soundTypes.getJSONObject(chP).getString("url");;
                    }else {
                        url=originalUrl;
                    }


                    long ca = System.currentTimeMillis();
                    String id = context.functions.constructID(User.getInstance().getUsername(), privateRoom.getId(), ca);

                    DialogMessageNew message=new DialogMessageNew(id,User.getInstance().getUsername(), PrivateRooms.getInterlocutorId(privateRoom.getId()),privateRoom.getId(),true,MimeType.audio,url,ca, MessageStates.WAITING);
                    DialogMessages.insertNewMessage(message);
                    MySocket.getInstance().sendDialogMessage(message);
                    adapter.addToStart(message,true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                stopAudio();
            }
        });
        builder.show();
    }
    private void requestPermissionCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.e(logTAG,"androidM+");
            context.requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_CAMERA_PERMISSION);
        } else {
            Log.e(logTAG,"show Alert");
            showAlert();
        }
    }
    private void uploadAudio(){
        Log.e("uploadAudio",AudioSavePathInDevice);
        File file=new File(AudioSavePathInDevice);

//        byte[] bytes = FileUtils.readFileToByteArray(file);
        AsyncHttpClient clientUpload = new AsyncHttpClient();
        RequestParams requestParamsImage = new RequestParams();
        try {
            Log.e("FILE", file.getName());
            requestParamsImage.put("source","android");
            requestParamsImage.put("filename", file.getName());
            requestParamsImage.put("file", Base64.encodeToString(readBytesFromFile(file),0));
            requestParamsImage.put("userid",User.getInstance().getUsername());
            Log.e("Request", requestParamsImage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        clientUpload.post(context, getString(R.string.url_upload_audio), requestParamsImage, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("statusCode", statusCode + "");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e("statusCodeAudio", statusCode + responseString);
                Log.e("ResponseAudio", responseString);
                try {
                    if (statusCode==200) {
                        JSONObject jsonAudio = new JSONObject(responseString);
                        JSONArray soundTypes=jsonAudio.getJSONArray("soundTypes");
                        chooseAudioType(soundTypes,jsonAudio.getString("original"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    public void uploadPhoto(File file, final DialogMessageNew message) {
        AsyncHttpClient clientUpload = new AsyncHttpClient();
        RequestParams requestParamsImage = new RequestParams();
        try {
            Log.e("FILE", file.getName());
            requestParamsImage.put("source","android");
            requestParamsImage.put("filename", file.getName());
            requestParamsImage.put("file", Base64.encodeToString(readBytesFromFile(file), Base64.DEFAULT));
            requestParamsImage.put("userid",User.getInstance().getUsername());
            Log.e("imageRequest", requestParamsImage.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        clientUpload.post(context, getString(R.string.url_upload_image), requestParamsImage, new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("statusCode", statusCode + "");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {

                Log.e("statusCodeImage", statusCode + responseString);
                Log.e("ResponseImage", responseString);
                try {
                    JSONObject jsonImage = new JSONObject(responseString);

                    String body = jsonImage.getString("big");
                    message.setBody(body);

                    if (images!=null){
                        images.add(message.getBody());
                    }

                    DialogMessages.insertNewMessage(message);
                    MySocket.getInstance().sendDialogMessage(message);

                    adapter.update(message.getId(),message);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
    private Uri getImageUri(Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public String getRealPathFromURI(Uri uri) {
        Cursor cursor =context.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }


    public void OnPhotoPicked(Bitmap bitmap){
        Uri tempUri = getImageUri(bitmap);
        File finalFile = new File(getRealPathFromURI(tempUri));

        long ca = System.currentTimeMillis();
        String id = context.functions.constructID(User.getInstance().getUsername(), privateRoom.getId(), ca);


        DialogMessageNew dialogMessageNew=new DialogMessageNew(id,User.getInstance().getUsername(), PrivateRooms.getInterlocutorId(privateRoom.getId()),privateRoom.getId(),true,MimeType.image,null,ca, MessageStates.WAITING);
        adapter.addToStart(dialogMessageNew,true);

        uploadPhoto(finalFile,dialogMessageNew);
    }
    private static byte[] readBytesFromFile(File file) {

        FileInputStream fileInputStream = null;
        byte[] bytesArray = null;

        try {

            bytesArray = new byte[(int) file.length()];

            //read file into bytes[]
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(bytesArray);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bytesArray;

    }



    private void sendTyping(){
        MySocket.getInstance().sendTyping(privateRoom.getId(),
                PrivateRooms.getInterlocutorId(privateRoom.getId()),
                typing);
    }
    private void initAdapter() {
        MessageHolders holders = new MessageHolders();


        //textMessages
        holders.registerContentType(DialogMessageNew.CONTENT_TYPE_TEXT,
                CustomIncomingDialogTextMessageViewHolder.class,
                R.layout.item_incoming_text_message,
                CustomOutcomingDialogTextMessageViewHolder.class,
                R.layout.item_custom_outcoming_text_message,this);


        //audioMessages
        holders.registerContentType(DialogMessageNew.CONTENT_TYPE_AUDIO,
                CustomIncomingDialogAudioMessageViewHolder.class,
                R.layout.item_custom_incoming_audio_message,
                CustomOutcomingDialogAudioMessageViewHolder.class,
                R.layout.item_custom_outcoming_audio_message,this);

        //image messages

        holders.registerContentType(DialogMessageNew.CONTENT_TYPE_IMAGE,
                CustomIncomingDialogImageMessageViewHolder.class,//must be incoming here
                R.layout.item_custom_incoming_image_message,
                CustomOutcomingDialogImageMessageViewHolder.class,
                R.layout.item_custom_outcoming_image_message,
                this);


        //unread message inform
        holders.registerContentType(DialogMessageNew.CONTENT_TYPE_UNREAD,
                UnreadDialogMessage.class,R.layout.unread_messages_layout,
                UnreadDialogMessage.class,R.layout.unread_messages_layout,
                this);



        adapter=new MessagesListAdapter<DialogMessageNew>(User.getInstance().getUsername(),holders,null);
        messagesList.setAdapter(adapter);

    }
    private void setupStatusView(){
        long secAgo=context.functions.getLastActivity(PrivateRooms.getInterlocutorId(privateRoom.getId()));
        if (secAgo==0){
            toolbar.setSubtitle(R.string.online);
        }else {
            toolbar.setSubtitle(R.string.offline);
        }
    }
    private void createAlertDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle(R.string.set_dialog_name);

        final View contentView=context.getLayoutInflater().inflate(R.layout.create_public_chat_dialog,null);
        builder.setView(contentView);

        final EditText editText=(EditText)contentView.findViewById(R.id.editText);

        builder.setPositiveButton(R.string.set, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final String group_name=editText.getText().toString();
                PrivateRooms.setRoomName(privateRoom.getId(),group_name);
                dialogInterface.cancel();
                toolbar.setTitle(group_name);
            }
        });
        dialog=builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
            }
        });
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Log.e(logTAG,"onCancel");
                editText.setText("");
            }
        });
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0){
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }else {
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    public void onBeforeExpand() {
        cancelAllAnimations();

        mSlideToCancel.setTranslationX(0f);
        mSlideToCancel.setAlpha(0f);
        mSlideToCancel.setVisibility(View.VISIBLE);
        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(1f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.start();

        mInputAnimator = inputCont.animate().alpha(0f).setDuration(mAnimationDuration);
        mInputAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                inputCont.setVisibility(View.INVISIBLE);
                mInputAnimator.setListener(null);
            }
        });
        mInputAnimator.start();

        mTime.setAlpha(0f);
        mTime.setVisibility(View.VISIBLE);
        mTimeAnimator = mTime.animate().translationY(0f).alpha(1f).setDuration(mAnimationDuration);
        mTimeAnimator.start();

    }

    @Override
    public void onExpand() {
        mStartTime = System.currentTimeMillis();
        invalidateTimer();
        Log.e(logTAG,"permission granted");

        AudioSavePathInDevice =Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                            CreateRandomAudioFileName(5) + "AudioRecording.3gp";
        if (currentsCallback!=null){
            currentsCallback.onNewPlay();
        }
        setCurrentsCallback(null);
        startRecord();

    }

    @Override
    public void onBeforeCollapse() {
        cancelAllAnimations();

        mSlideToCancelAnimator = mSlideToCancel.animate().alpha(0f).setDuration(mAnimationDuration);
        mSlideToCancelAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSlideToCancel.setVisibility(View.INVISIBLE);
                mSlideToCancelAnimator.setListener(null);
            }
        });
        mSlideToCancelAnimator.start();

        inputCont.setAlpha(0f);
        inputCont.setVisibility(View.VISIBLE);
        mInputAnimator = inputCont.animate().alpha(1f).setDuration(mAnimationDuration);
        mInputAnimator.start();

        mTimeAnimator = mTime.animate().translationY(mTime.getHeight()).alpha(0f).setDuration(mAnimationDuration);
        mTimeAnimator.setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mTime.setVisibility(View.GONE);
                mTimeAnimator.setListener(null);
            }
        });
        mTimeAnimator.start();

    }

    @Override
    public void onCollapse(boolean isCancel) {
        stopTimer();
        Log.e(logTAG,"onCollapse");
        try {
            mediaRecorder.stop();
        }catch (RuntimeException e){
            e.printStackTrace();
        }
        makeDisabledForSomeTime();
        if (isCancel) {
            vibrator.vibrate(500);
            context.functions.showHint("Canceled");
        } else {
            long duarationms=System.currentTimeMillis()-mStartTime;
            Log.e(logTAG,"duration "+duarationms);
            if (duarationms/1000d>1d) {
                uploadAudio();
            }else {
                showHoldLongerLayout(true);
            }
           }

    }

    @Override
    public void onOffsetChanged(float offset, boolean isCancel) {
        mSlideToCancel.setTranslationX(-mHoldingButtonLayout.getWidth() * offset);
        mSlideToCancel.setAlpha(1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * offset);
    }
    private void invalidateTimer() {
        mTimerRunnable = new Runnable() {
            @Override
            public void run() {
                mTime.setText(getFormattedTime());
                invalidateTimer();
            }
        };

        mTime.postDelayed(mTimerRunnable, TIME_INVALIDATION_FREQUENCY);
    }

    private void stopTimer() {
        if (mTimerRunnable != null) {
            mTime.getHandler().removeCallbacks(mTimerRunnable);

        }
    }

    private void cancelAllAnimations() {
        if (mInputAnimator != null) {
            mInputAnimator.cancel();
        }

        if (mSlideToCancelAnimator != null) {
            mSlideToCancelAnimator.cancel();
        }

        if (mTimeAnimator != null) {
            mTimeAnimator.cancel();
        }
    }

    private String getFormattedTime() {
        return mFormatter.format(new Date(System.currentTimeMillis() - mStartTime));
    }
    public void startRecord(){
        mediaRecorder.reset();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String CreateRandomAudioFileName(int string){
        StringBuilder stringBuilder = new StringBuilder( string );
        int i = 0 ;
        while(i < string ) {
            stringBuilder.append(RandomAudioFileName.
                    charAt(random.nextInt(RandomAudioFileName.length())));

            i++ ;
        }
        return stringBuilder.toString();
    }
    public void  playAudio(String url) {

        try {
            mediaPlayer.reset();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mediaPlayer.setDataSource(url);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer1) {
                    if (currentsCallback!=null){
                        currentsCallback.onReady(mediaPlayer.getDuration());
                    }

                    mediaPlayer.start();
                }
            });
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopAudio(){
        mediaPlayer.stop();
    }
    private void createMediaPlayer(){
        mediaPlayer=new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer1) {
                Log.e(logTAG+"MP","onComplete");
                if (currentsCallback!=null){
                    currentsCallback.onComplete();
                }

            }
        });
    }
    private void createMediaRecorder(){
        mediaRecorder=new MediaRecorder();

    }
    private void makeDisabledForSomeTime(){
        mHoldingButtonLayout.setButtonEnabled(false);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHoldingButtonLayout.setButtonEnabled(true);
            }
        },1000);
    }
    private void showHoldLongerLayout(boolean show){
        if (show) {
            vibrator.vibrate(500);
            holdLongerLayout.setAlpha(0f);
            holdLongerLayout.setVisibility(View.VISIBLE);
            mHoldLongerAnimator = holdLongerLayout.animate().translationY(0f).alpha(1f).setDuration(mAnimationDuration);
            mHoldLongerAnimator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mHoldLongerAnimator.setListener(null);
                            showHoldLongerLayout(false);
                        }
                    },1000);

                }
            });
            mHoldLongerAnimator.start();
        }else {
            mHoldLongerAnimator = holdLongerLayout.animate().translationY(mHoldingButtonLayout.getHeight()).alpha(0f).setDuration(mAnimationDuration);
            mHoldLongerAnimator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    holdLongerLayout.setVisibility(View.GONE);
                    mHoldLongerAnimator.setListener(null);
                }
            });
            mHoldLongerAnimator.start();
        }


    }
    public void openFullSizeImage(String url){
        ImageSliderFragment imageSliderFragment=new ImageSliderFragment();
        Bundle bundle=new Bundle();
        imageSliderFragment.setArguments(bundle);
        bundle.putStringArrayList("images",images);
        bundle.putString("current",url);
        context.functions.ReplaceFragmentWithStack(imageSliderFragment);
    }

}
