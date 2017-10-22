package com.support.robigroup.ututor.features.chat.custom.media.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.stfalcon.chatkit.utils.RoundedImageView;
import com.support.robigroup.ututor.R;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;


public class CustomIncomingMessageViewHolder extends MessageHolders.IncomingTextMessageViewHolder<ChatMessage> {

    private ImageView image;
    private TextView tvTime;

    public CustomIncomingMessageViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
        tvTime = itemView.findViewById(R.id.messageTime);
        if (image != null && image instanceof RoundedImageView) {
            ((RoundedImageView) image).setCorners(
                    com.stfalcon.chatkit.R.dimen.message_bubble_corners_radius,
                    com.stfalcon.chatkit.R.dimen.message_bubble_corners_radius,
                    com.stfalcon.chatkit.R.dimen.message_bubble_corners_radius,
                    com.stfalcon.chatkit.R.dimen.message_bubble_corners_radius
            );
        }
    }

    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
        if (image != null && getImageLoader() != null) {
            getImageLoader().loadImage(image, message.getIconUrl());
        }
    }
}
