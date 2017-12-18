package com.support.robigroup.ututor.data.network

import com.support.robigroup.ututor.features.chat.model.ChatMessage

import java.io.File
import java.util.HashMap

import io.reactivex.Flowable
import retrofit2.Response


interface NetworkHelper {

    fun sendAudioMessage(file: File): Flowable<Response<ChatMessage>>

    fun sendImageTextMessage(messageText: String? = null, file64base: String? = null): Flowable<Response<ChatMessage>>

    fun getChatMessages(chatId: String): Flowable<Response<List<ChatMessage>>>

}
