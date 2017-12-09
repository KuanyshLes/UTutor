package com.support.robigroup.ututor.ui.chat.holders

import android.view.View
import android.widget.ImageView
import android.widget.TextView

import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.utils.DateFormatter
import com.stfalcon.chatkit.utils.RoundedImageView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.chat.model.ChatMessage


class IncomingImageMessageVH(itemView: View) : MessageHolders.IncomingTextMessageViewHolder<ChatMessage>(itemView) {

    private val image: ImageView? = itemView.findViewById(R.id.image)

    init {
        if (image != null && image is RoundedImageView) {
            image.setCorners(
                    com.stfalcon.chatkit.R.dimen.message_bubble_corners_radius,
                    com.stfalcon.chatkit.R.dimen.message_bubble_corners_radius,
                    com.stfalcon.chatkit.R.dimen.message_bubble_corners_radius,
                    com.stfalcon.chatkit.R.dimen.message_bubble_corners_radius
            )
        }
    }

    override fun onBind(message: ChatMessage) {
        super.onBind(message)
        time.text = DateFormatter.format(message.createdAt, DateFormatter.Template.TIME)
        if (image != null && imageLoader != null) {
            imageLoader.loadImage(image, message.iconUrl)
        }
    }
}
