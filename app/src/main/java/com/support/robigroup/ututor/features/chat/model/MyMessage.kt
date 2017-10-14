package com.support.robigroup.ututor.features.chat.model

import com.stfalcon.chatkit.commons.models.IMessage
import com.stfalcon.chatkit.commons.models.IUser
import com.stfalcon.chatkit.commons.models.MessageContentType
import com.support.robigroup.ututor.Constants
import io.realm.RealmObject
import java.text.SimpleDateFormat
import java.util.*

data class MyMessage(
        private val customMessage: CustomMessage,
        var User: User?
) : IMessage, MessageContentType{
    fun getImageUrl(): String? = customMessage.FilePath

    fun getImageIconUrl(): String? = customMessage.FileOpenIcon

    override fun getId(): String = customMessage.Id.toString()

    override fun getCreatedAt(): Date =
        SimpleDateFormat("HH:mm").parse(customMessage.Time)


    override fun getUser(): IUser? = User

    override fun getText(): String? = customMessage.Text
}

data class MyHistoryMessage(
        private val customMessage: CustomMessageHistory
) : IMessage, MessageContentType{
    private val user = User(customMessage.Owner!!,customMessage.Owner,null,true)

    fun getImageUrl(): String? = customMessage.FilePath

    fun getImageIconUrl(): String? = customMessage.FileOpenIcon

    override fun getId(): String = customMessage.Id.toString()

    override fun getCreatedAt(): Date  = SimpleDateFormat(Constants.TIMEFORMAT).parse(customMessage.Time)

    override fun getUser(): IUser? = user

    override fun getText(): String? = customMessage.Text
}

open class CustomMessage(
        var Id: Long = 0,
        var Time: String = "",
        var FileOpenIcon: String? = null,
        var FilePath: String? = null,
        var Text: String? = null,
        var Owner: String = "Learner"
): RealmObject()

open class CustomMessageHistory(
        var Id: Long = 0,
        var Time: String = "",
        var FileOpenIcon: String? = null,
        var FilePath: String? = null,
        var Owner: String? = null,
        var Text: String? = null
)