package com.support.robigroup.ututor.data.network

import com.support.robigroup.ututor.ui.chat.model.ChatMessage

import java.io.File

import io.reactivex.Flowable
import retrofit2.Response


interface NetworkHelper {

    fun sendAudioMessage(file: File): Flowable<Response<ChatMessage>>

    fun sendImageTextMessage(messageText: String? = null, file64base: String? = null): Flowable<Response<ChatMessage>>

    fun getChatMessages(chatId: String): Flowable<Response<List<ChatMessage>>>

//    fun downloadAudio(url: String, onProgress: ()->Unit, onFinish: ()->Unit, onError: ()->Unit)
//
//    fun cancelDownload(url: String, onCancelled: ()->Unit)

}
