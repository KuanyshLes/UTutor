package com.support.robigroup.ututor.ui.chat.holders;

import android.view.View;
import android.widget.ImageView;

import com.stfalcon.chatkit.messages.MessageHolders;
import com.stfalcon.chatkit.utils.RoundedImageView;
import com.support.robigroup.ututor.R;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;

public class OutcomingImageMessageVH
        extends MessageHolders.OutcomingTextMessageViewHolder<ChatMessage> {

    private ImageView image;

    public OutcomingImageMessageVH(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.image);
        if (image != null && image instanceof RoundedImageView) {
            ((RoundedImageView) image).setCorners(
                    R.dimen.image_corner_radius,
                    R.dimen.image_corner_radius,
                    R.dimen.image_corner_radius,
                    R.dimen.image_corner_radius
            );
        }
    }

    @Override
    public void onBind(ChatMessage message) {
        super.onBind(message);
        time.setTextColor(time.getContext().getResources().getColor(R.color.colorGrey));
        if (image != null && getImageLoader() != null) {
            getImageLoader().loadImage(image, message.getIconUrl());
        }
    }
}
