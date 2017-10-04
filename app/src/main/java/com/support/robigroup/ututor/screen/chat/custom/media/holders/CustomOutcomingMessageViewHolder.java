package com.support.robigroup.ututor.screen.chat.custom.media.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.stfalcon.chatkit.utils.RoundedImageView;
import com.support.robigroup.ututor.R;
import com.support.robigroup.ututor.screen.chat.model.MyMessage;

public class CustomOutcomingMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<MyMessage> {

    private ImageView image;
    private TextView tvTime;

    public CustomOutcomingMessageViewHolder(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
        tvTime = itemView.findViewById(R.id.time);
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
    public void onBind(MyMessage message) {
        super.onBind(message);
        text.setVisibility( message.getText()==null ? View.GONE : View.VISIBLE);
        tvTime.setText(DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME));
        if (image != null && getImageLoader() != null) {
            getImageLoader().loadImage(image, message.getImageIconUrl());
        }
    }

}
