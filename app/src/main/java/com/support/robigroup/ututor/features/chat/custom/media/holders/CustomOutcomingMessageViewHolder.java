package com.support.robigroup.ututor.features.chat.custom.media.holders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.DateFormatter;
import com.stfalcon.chatkit.utils.RoundedImageView;
import com.support.robigroup.ututor.R;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;

public class CustomOutcomingMessageViewHolder
        extends MessageHolders.OutcomingTextMessageViewHolder<ChatMessage> {

    private ImageView image;
    private TextView tvTime;

    public CustomOutcomingMessageViewHolder(View itemView) {
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
        String myText = DateFormatter.format(message.getCreatedAt(), DateFormatter.Template.TIME);
        tvTime.setText(myText);
        tvTime.setTextColor(tvTime.getContext().getResources().getColor(R.color.colorGrey));
        if (image != null && getImageLoader() != null) {
            getImageLoader().loadImage(image, message.getIconUrl());
        }
    }


//    app:incomingBubblePaddingBottom="@dimen/bubble_corners_radius"
//    app:incomingBubblePaddingTop="@dimen/bubble_corners_radius"
//    app:incomingBubblePaddingLeft="@dimen/bubble_corners_radius"
//    app:incomingBubblePaddingRight="@dimen/bubble_corners_radius"
//    app:outcomingBubblePaddingBottom="@dimen/bubble_corners_radius"
//    app:outcomingBubblePaddingTop="@dimen/bubble_corners_radius"
//    app:outcomingBubblePaddingLeft="@dimen/bubble_corners_radius"
//    app:outcomingBubblePaddingRight="@dimen/bubble_corners_radius"

}
