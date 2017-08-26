package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.Constants.KEY_TOKEN
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.ChatLesson
import com.support.robigroup.ututor.commons.MessagesResponse
import com.support.robigroup.ututor.model.content.*
import com.support.robigroup.ututor.screen.chat.model.CustomMessage
import com.support.robigroup.ututor.screen.chat.model.MyMessage
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

    fun postLearnerReady(): Flowable<Response<ResponseBody>> = RestAPI.getApi().postChatReady()

    fun postChatComplete(): Flowable<Response<ResponseBody>> = RestAPI.getApi().postChatComplete()

    fun getChatInformation(): Flowable<Response<ChatLesson>> = RestAPI.getApi().getInformationAboutChat()

    fun sendTextMessage(messageText: String,file64base: String? = null): Flowable<Response<CustomMessage>> =
            if(file64base != null) RestAPI.getApi().postTextMessage(messageText,file64base)
            else RestAPI.getApi().postTextMessage(messageText)

}