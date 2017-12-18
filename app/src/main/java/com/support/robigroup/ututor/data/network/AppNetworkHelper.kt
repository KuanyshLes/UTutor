package com.support.robigroup.ututor.data.network

import android.content.Context
import android.net.Uri

import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.di.ApplicationContext
import com.support.robigroup.ututor.features.chat.model.ChatMessage

import java.io.File

import javax.inject.Inject
import javax.inject.Singleton

import io.reactivex.Flowable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import android.webkit.MimeTypeMap
import java.util.HashMap


@Singleton
class AppNetworkHelper @Inject
constructor(@param:ApplicationContext private val mContext: Context) : NetworkHelper {

    override fun sendAudioMessage(file: File): Flowable<Response<ChatMessage>> {
        val resolver = getMimeType(file.absolutePath)
        val type = MediaType.parse(resolver)
        val filePart = MultipartBody.Part.createFormData("File", file.name, RequestBody.create(type, file))
        return RestAPI.getUploadApi().postMessageAudio(filePart)
    }

    override fun sendImageTextMessage(messageText: String?, file64base: String?): Flowable<Response<ChatMessage>> =
        if(file64base != null&&messageText!=null){
            val res: HashMap<String, String> = HashMap()
            res.put("File",file64base)
            res.put("Message",messageText)
            RestAPI.getUploadApi().postMessagePhoto(res)
        } else if(file64base!=null) {
            val res: HashMap<String, String> = HashMap()
            res.put("File",file64base)
            RestAPI.getUploadApi().postMessagePhoto(res)
        }else if(messageText!=null) RestAPI.getUploadApi().postTextMessage(messageText)
        else Flowable.empty()

    override fun getChatMessages(chatId: String): Flowable<Response<List<ChatMessage>>> {
        return Flowable.empty()
    }

    private fun getMimeType(url: String): String {
        var type: String = ""
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }
        return type
    }
}
