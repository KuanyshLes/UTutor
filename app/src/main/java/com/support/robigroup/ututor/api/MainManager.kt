package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.Constants.KEY_TOKEN
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.ui.chat.model.ChatMessage
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response


class MainManager(
        private val TOKEN: String = SingletonSharedPref.getInstance().getString(KEY_TOKEN),
        private val api: RestAPI = RestAPI()) {

    fun getSubjects(type: Int): Single<Response<List<Subject>>>
        = RestAPI.getApi().getSubjects(type)

    fun getTeachers(classRoom: Int,language: String,subjectId: Int, type: Int): Single<Response<List<Teacher>>>
        = RestAPI.getApi().getTeachers(
                classRoom,
                language,
                subjectId,
                type
        )

    fun getBalance(): Single<Response<Profile>>
            = RestAPI.getApi().getBalance()

    fun postLessonRequest(teacherId: String, subjectId: Int, language: String, classNumber: Int):
            Single<Response<LessonRequestForTeacher>>
        = RestAPI.getApi().postLessonRequest(teacherId, subjectId, language, classNumber)

    fun postLearnerReady(): Single<Response<ResponseBody>> = RestAPI.getApi().postChatReady()

    fun postChatComplete(): Single<Response<ChatLesson>> = RestAPI.getApi().postChatComplete()

    fun postRequestCancel(id: String): Single<Response<ResponseBody>> = RestAPI.getApi().postCancelRequest(id)

    fun evalChat(rating: Int,lessonId: Int): Single<Response<ResponseBody>> = RestAPI.getApi().evalChat(rating,lessonId)

    fun getChatInformation(): Single<Response<ChatLesson>> = RestAPI.getApi().getInformationAboutChat()

    fun getHistory(): Single<Response<List<ChatHistory>>> = RestAPI.getApi().getHistory()

    fun getHistoryMessages(chatId: Int): Single<Response<List<ChatMessage>>> = RestAPI.getApi().getHistoryMessages(chatId)

    fun resetPassword(oldPassword: String, confirmPassword: String, newPassword: String) :Single<Response<ResponseBody>>{
        return RestAPI.getApi().resetPassword(oldPassword,confirmPassword,newPassword).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun sendMessage(messageText: String? = null, file64base: String? = null): Single<Response<ChatMessage>> =
            if(file64base != null&&messageText!=null){
                val res: HashMap<String,String> = HashMap()
                res.put("File",file64base)
                res.put("Message",messageText)
                RestAPI.getApi().postMessagePhoto(res)
            } else if(file64base!=null) {
                val res: HashMap<String,String> = HashMap()
                res.put("File",file64base)
                RestAPI.getApi().postMessagePhoto(res)
            }else{
                RestAPI.getApi().postTextMessage(messageText!!)
            }


}