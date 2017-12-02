package com.support.robigroup.ututor.ui.chat.custom_holders;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;


public class IncomingAudioMessageVH extends MessageHolders.BaseIncomingMessageViewHolder<ChatMessage> {

    View itemView;

    ImageButton playPause;
    SeekBar seekBar;
    TextView timeTv;



    Handler handler=new Handler();


    int duration,stepToUpdate,progress;
    private ViewGroup bubble;


    public IncomingAudioMessageVH(View itemView) {
        super(itemView);
        bubble = itemView.findViewById(com.stfalcon.chatkit.R.id.bubble);

    }

    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);
        if (bubble != null) {
            bubble.setSelected(isSelected());
        }
    }

}
