package com.support.robigroup.ututor.data.network

import android.content.Context
import android.webkit.MimeTypeMap
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.di.ApplicationContext
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import io.reactivex.Flowable
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import java.io.File
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


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

//    override fun downloadAudio(url: String, onProgress: () -> Unit, onFinish: () -> Unit, onError: () -> Unit) {
//
//    }
//
//    override fun cancelDownload(url: String, onCancelled: () -> Unit) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//    }

}
