package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.Constants.KEY_TOKEN
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.ResponseBody
import retrofit2.Response


class MainManager(
        private val TOKEN: String = SingletonSharedPref.getInstance().getString(KEY_TOKEN),
        private val api: RestAPI = RestAPI()) {

    fun getSubjects(type: Int): Flowable<Response<List<Subject>>>
        = RestAPI.getApi().getSubjects(type)

    fun getTeachers(classRoom: Int,language: String,subjectId: Int, type: Int): Flowable<Response<List<Teacher>>>
        = RestAPI.getApi().getTeachers(
                classRoom,
                language,
                subjectId,
                type
        )

    fun getBalance(): Flowable<Response<Profile>>
            = RestAPI.getApi().getBalance()

    fun postLessonRequest(teacherId: String, subjectId: Int, language: String, classNumber: Int):
            Flowable<Response<LessonRequestForTeacher>>
        = RestAPI.getApi().postLessonRequest(teacherId, subjectId, language, classNumber)

    fun postLearnerReady(): Flowable<Response<ResponseBody>> = RestAPI.getApi().postChatReady()

    fun postChatComplete(): Flowable<Response<ChatLesson>> = RestAPI.getApi().postChatComplete()

    fun postRequestCancel(id: String): Flowable<Response<ResponseBody>> = RestAPI.getApi().postCancelRequest(id)

    fun evalChat(rating: Int,lessonId: Int): Flowable<Response<ResponseBody>> = RestAPI.getApi().evalChat(rating,lessonId)

    fun getChatInformation(): Flowable<Response<ChatLesson>> = RestAPI.getApi().getInformationAboutChat()

    fun getHistory(): Flowable<Response<List<ChatHistory>>> = RestAPI.getApi().getHistory()

    fun getHistoryMessages(chatId: Int): Flowable<Response<List<ChatMessage>>> = RestAPI.getApi().getHistoryMessages(chatId)

    fun resetPassword(oldPassword: String, confirmPassword: String, newPassword: String) :Flowable<Response<ResponseBody>>{
        return RestAPI.getApi().resetPassword(oldPassword,confirmPassword,newPassword).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

    fun sendMessage(messageText: String? = null, file64base: String? = null): Flowable<Response<ChatMessage>> =
            if(file64base != null&&messageText!=null){
                val res: HashMap<String,String> = HashMap()
                res.put("File",file64base)
                res.put("Message",messageText)
                RestAPI.getApi().postMessagePhoto(res)
            } else if(file64base!=null) {
                val res: HashMap<String,String> = HashMap()
                res.put("File",file64base)
                RestAPI.getApi().postMessagePhoto(res)
            }else if(messageText!=null) RestAPI.getApi().postTextMessage(messageText)
            else Flowable.empty()

//    fun sendFileMessage(encodedString:String): Flowable<Response<CustomMessage>>{
//        return Flowable.create( {
//            subscriber ->
//            val connection: HttpURLConnection? = null
//            try {
//                val url = URL(Constants.BASE_URL+"api/lesson/chat/message/file")
//                val con = url.openConnection() as HttpURLConnection
//                val data = JSONObject()
//                data.put("FilePath",encodedString)
//                con.requestMethod = "POST"
//                con.useCaches = false
//                con.doInput = true
//                con.doOutput = true
//                con.setRequestProperty("Connection", "Keep-Alive")
//                con.doOutput = true
//
//                val os = con.outputStream
//                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
//
//                //make request
//                writer.write(data.toString())
//                writer.flush()
//                writer.close()
//                val reader = BufferedReader(InputStreamReader(con.getInputStream()))
//                val sb = StringBuilder()
//                var line: String? = null
//                line = reader.readLine()
//                while (line != null) {
//                    sb.append(line)
//                    line = reader.readLine()
//                }
//                val res: String= sb.toString()
//                subscriber.onNext(Response.success(Gson().fromJson(res,CustomMessage::class.java)))
//                subscriber.onComplete()
//            } catch (ex: Exception) {
//                subscriber.onError(ex)
//            }
//        }, BackpressureStrategy.LATEST)
//    }


}