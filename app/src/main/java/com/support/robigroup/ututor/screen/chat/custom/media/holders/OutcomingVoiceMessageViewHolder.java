package com.support.robigroup.ututor.screen.chat.custom.media.holders;

import android.view.View;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.support.robigroup.ututor.R;
import com.support.robigroup.ututor.commons.FormatUtils;
import com.support.robigroup.ututor.commons.model.Message;

/*
 * Created by troy379 on 05.04.17.
 */
public class OutcomingVoiceMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<Message> {

    private TextView tvDuration;
    private TextView tvTime;

    public OutcomingVoiceMessageViewHolder(View itemView) {
        super(itemView);
        tvDuration = (TextView) itemView.findViewById(R.id.duration);
        tvTime = (TextView) itemView.findViewById(R.id.time);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        tvDuration.setText(
                FormatUtils.getDurationString(
                        message.getVoice().getDuration()));
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
    }
}
