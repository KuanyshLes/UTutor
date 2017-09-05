package com.support.robigroup.ututor.screen.chat.custom.media.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.support.robigroup.ututor.screen.chat.model.MyMessage;

/**
 * Created by Bimurat Mukhtar on 04.09.2017.
 */

public class CustomOutcomingMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<MyMessage> {

    private ImageView image;
    private TextView tvTime;

    public CustomOutcomingMessageViewHolder(View itemView) {
        super(itemView);

    }

    @Override
    public void onBind(MyMessage message) {
        super.onBind(message);
    }
}
