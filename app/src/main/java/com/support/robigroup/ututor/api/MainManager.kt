package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.Constants.KEY_TOKEN
import com.support.robigroup.ututor.commons.ChatLesson
import com.support.robigroup.ututor.model.content.LessonRequestForTeacher
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.chat.model.CustomMessage
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.Response


class MainManager(
        private val TOKEN: String = SingletonSharedPref.getInstance().getString(KEY_TOKEN),
        private val api: RestAPI = RestAPI()) {

    fun getLessons(classRoom: Int,lang: String): Flowable<Response<List<Subject>>>
        = RestAPI.getApi().getSubjects(classRoom,lang)

    fun getTeachers(classRoom: Int = 10,language: String ="kk-KZ",subjectId: Int = 2,topicId: Int=2): Flowable<Response<List<Teacher>>>
        = RestAPI.getApi().getTeachersByTopic(
                classRoom,
                language,
                subjectId,
                topicId
        )

    fun getTopics(subjectId: Int): Flowable<Response<List<TopicItem>>>
        = RestAPI.getApi().getTopicsBySubject(subjectId)

    fun postLessonRequest(teacherId: String, topicId: Int): Flowable<Response<LessonRequestForTeacher>>
        = RestAPI.getApi().postLessonRequest(teacherId,topicId)

    fun postLearnerReady(): Flowable<Response<String>> = RestAPI.getApi().postChatReady()

    fun postChatComplete(): Flowable<Response<ResponseBody>> = RestAPI.getApi().postChatComplete()

    fun getChatInformation(): Flowable<Response<ChatLesson>> = RestAPI.getApi().getInformationAboutChat()

    fun sendMessage(messageText: String? = null, file64base: String? = null): Flowable<Response<CustomMessage>> =
            if(file64base != null&&messageText!=null) RestAPI.getApi().postTextMessageWithPhoto(messageText,file64base)
            else if(file64base!=null) RestAPI.getApi().postPhotoMessage(file64base)
            else if(messageText!=null) RestAPI.getApi().postTextMessage(messageText)
            else Flowable.empty()

//    fun sendFileMessage(encodedString:String): Flowable<Response<CustomMessage>>{
//        return Flowable.create( {
//            subscriber ->
//            val connection: HttpURLConnection? = null
//            try {
//                val url = URL(Constants.BASE_URL+"api/lesson/chat/message/file")
//                val con = url.openConnection() as HttpURLConnection
//                val data = JSONObject()
//                data.put("File",encodedString)
//                con.requestMethod = "POST"
//                con.useCaches = false
//                con.doInput = true
//                con.doOutput = true
//                con.setRequestProperty("Connection", "Keep-Alive")
//                con.doOutput = true
//
//                val os = con.getOutputStream()
//                val writer = BufferedWriter(OutputStreamWriter(os, "UTF-8"))
//
//                //make request
//                writer.write(data)
//                writer.flush()
//                writer.close()
//                reader = BufferedReader(InputStreamReader(con.getInputStream()))
//                val sb = StringBuilder()
//                var line: String? = null
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line)
//                }
//                return Flowable.
//            } catch (ex: Exception) {
//                subscriber.onError(ex)
//            }
//        },BackpressureStrategy.LATEST)
//    }
}