package com.support.robigroup.ututor.ui.chat.custom_holders;

import android.view.View;
import android.view.ViewGroup;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;


public class IncomingVoiceMessageVH extends MessageHolders.BaseIncomingMessageViewHolder<ChatMessage> {

    private ViewGroup bubble;


    public IncomingVoiceMessageVH(View itemView) {
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
