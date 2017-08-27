package com.support.robigroup.ututor.screen.chat.model

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.commons.models.MessageContentType
import io.realm.RealmObject
import java.text.SimpleDateFormat
import java.util.*

data class MyMessage(
        private val customMessage: CustomMessage,
        var User: User?
) : IMessage, MessageContentType.Image{
    override fun getImageUrl(): String? = customMessage.File ?: null

    override fun getId(): String = customMessage.Id.toString()

    override fun getCreatedAt(): Date = SimpleDateFormat("HH:mm").parse(customMessage.Time ?: "00:00")

    override fun getUser(): IUser? = User

    override fun getText(): String? = customMessage.Message
}

open class CustomMessage(
        var Id: Long? = null,
        var Time: String? = null,
        var FileThumbnail: String? = null,
        var File: String? = null,
        var Message: String? = null
): RealmObject()