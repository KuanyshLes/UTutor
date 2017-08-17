package com.support.robigroup.ututor.screen.chat;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.view.View;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;
import com.support.robigroup.ututor.R;
import com.support.robigroup.ututor.SignalRService;
import com.support.robigroup.ututor.commons.fixtures.MessagesFixtures;
import com.support.robigroup.ututor.model.content.Teacher;
import com.support.robigroup.ututor.screen.chat.model.Message;
import com.support.robigroup.ututor.screen.chat.custom.media.holders.IncomingVoiceMessageViewHolder;
import com.support.robigroup.ututor.screen.chat.custom.media.holders.OutcomingVoiceMessageViewHolder;
import com.support.robigroup.ututor.screen.main.MainActivity;

import org.jetbrains.annotations.NotNull;

import static com.support.robigroup.ututor.commons.ExtensionsKt.logd;

public class ChatActivity extends DemoMessagesActivity
        implements MessageInput.InputListener,
        MessageInput.AttachmentsListener,
        MessageHolders.ContentChecker<Message>,
        DialogInterface.OnClickListener,
        FinishDialog.NoticeDialogListener {

    private final Context mContext = this;
    private SignalRService mService;
    private boolean mBound = false;
    private Teacher teacher;

    private MessagesList messagesList;

    private static final byte CONTENT_TYPE_VOICE = 1;
    private static final String KEY_TEACHER = "teacher";

    public static void open(Context context,Teacher teacher) {
        context.startActivity(new Intent(context, ChatActivity.class).putExtra(KEY_TEACHER,teacher));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        teacher = getIntent().getParcelableExtra(KEY_TEACHER);

        findViewById(R.id.text_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFinishDialog();
            }
        });

        logd("beforeStartingService");
        Intent intent = new Intent();
        intent.setClass(mContext, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        this.messagesList = (MessagesList) findViewById(R.id.messagesList);
        initAdapter();

        MessageInput input = (MessageInput) findViewById(R.id.input);
        input.setInputListener(this);
        input.setAttachmentsListener(this);
    }

    private void initAdapter() {
        MessageHolders holders = new MessageHolders()
                .registerContentType(
                        CONTENT_TYPE_VOICE,
                        IncomingVoiceMessageViewHolder.class,
                        R.layout.item_custom_incoming_voice_message,
                        OutcomingVoiceMessageViewHolder.class,
                        R.layout.item_custom_outcoming_voice_message,
                        this);


        super.messagesAdapter = new MessagesListAdapter<>(super.senderId, holders, null);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        this.messagesList.setAdapter(super.messagesAdapter);
    }

    @Override
    protected void onStop() {
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
        super.onStop();
    }

    public void sendMessage(View view) {
//        if (mBound) {
//            // Call a method from the SignalRService.
//            // However, if this call were something that might hang, then this request should
//            // occur in a separate thread to avoid slowing down the activity performance.
//            EditText editText = (EditText) findViewById(R.Id.edit_message);
//            EditText editText_Receiver = (EditText) findViewById(R.Id.edit_receiver);
//            if (editText != null && editText.getText().length() > 0) {
//                String receiver = editText_Receiver.getText().toString();
//                String message = editText.getText().toString();
//                mService.sendMessage_To(receiver, message);
//            }
//        }
    }

    private void showFinishDialog(){
        DialogFragment dialogFragment = new FinishDialog();
        dialogFragment.show(getSupportFragmentManager(),"finishDialog");
    }

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    @Override
    public boolean onSubmit(CharSequence input) {
        super.messagesAdapter.addToStart(
                MessagesFixtures.getTextMessage(input.toString()), true);
        if(mBound){
//            mService.sendMessage_To();
        }
        return true;
    }

    @Override
    public void onAddAttachments() {
        new AlertDialog.Builder(this)
                .setItems(R.array.view_types_dialog, this)
                .show();
    }

    @Override
    public boolean hasContentFor(Message message, byte type) {
        switch (type) {
            case CONTENT_TYPE_VOICE:
                return message.getVoice() != null
                        && message.getVoice().getUrl() != null
                        && !message.getVoice().getUrl().isEmpty();
        }
        return false;
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        switch (i) {
            case 0:
                messagesAdapter.addToStart(MessagesFixtures.getImageMessage(), true);
                break;
            case 1:
                messagesAdapter.addToStart(MessagesFixtures.getVoiceMessage(), true);
                break;
        }
    }


    @Override
    public void onDialogPositiveClick(@NotNull DialogFragment dialog) {
        dialog.dismiss();
        startActivity(new Intent(ChatActivity.this,MainActivity.class));
        finish();
    }
}
