package com.support.robigroup.ututor.ui.chat.holders

import android.view.View
import android.widget.ImageView

import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.utils.RoundedImageView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.chat.model.ChatMessage

class OutcomingImageMessageVH(itemView: View) : MessageHolders.OutcomingTextMessageViewHolder<ChatMessage>(itemView) {

    private val image: ImageView? = itemView.findViewById(R.id.image)

    init {
        if (image != null && image is RoundedImageView) {
            image.setCorners(
                    R.dimen.image_corner_radius,
                    R.dimen.image_corner_radius,
                    R.dimen.image_corner_radius,
                    R.dimen.image_corner_radius
            )
        }
    }

    override fun onBind(message: ChatMessage) {
        super.onBind(message)
        time.setTextColor(time.context.resources.getColor(R.color.colorGrey))
        if (image != null && imageLoader != null) {
            imageLoader.loadImage(image, message.iconUrl)
        }
        if(message.text==null || message.text==""){
            text.visibility = View.GONE
        }
        if (bubble != null) {
            val valueInPixels = bubble.context.resources.getDimension(R.dimen.bubble_padding).toInt()
            bubble.setPadding(valueInPixels,
                    valueInPixels,
                    valueInPixels,
                    valueInPixels)
        }

    }
}
