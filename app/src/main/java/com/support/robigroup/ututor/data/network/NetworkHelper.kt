package com.support.robigroup.ututor.data.network

import com.support.robigroup.ututor.ui.chat.model.ChatMessage

import java.io.File

import io.reactivex.Single
import retrofit2.Response


interface NetworkHelper {

    fun sendAudioMessage(file: File): Single<Response<ChatMessage>>

    fun sendImageTextMessage(messageText: String? = null, file64base: String? = null): Single<Response<ChatMessage>>

    fun getChatMessages(chatId: String): Single<Response<List<ChatMessage>>>?

//    fun downloadAudio(url: String, onProgress: ()->Unit, onFinish: ()->Unit, onError: ()->Unit)
//
//    fun cancelDownload(url: String, onCancelled: ()->Unit)

}
