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
) : IMessage, MessageContentType.Image, MessageContentType{
    override fun getImageUrl(): String? = customMessage.File

    override fun getId(): String = customMessage.Id.toString()

    override fun getCreatedAt(): Date = SimpleDateFormat("HH:mm").parse(customMessage.Time)

    override fun getUser(): IUser? = User

    override fun getText(): String? = customMessage.Message
}

open class CustomMessage(
        var Id: Long = 0,
        var Time: String = "",
        var FileThumbnail: String? = null,
        var File: String? = null,
        var Message: String? = null
): RealmObject()