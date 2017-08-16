package com.support.robigroup.ututor.screen.main

import com.support.robigroup.ututor.Constants.KEY_TOKEN
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.model.content.*
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.Flowable
import retrofit2.Response


class MainManager(
        private val TOKEN: String = SingletonSharedPref.getInstance().getString(KEY_TOKEN),
        private val api: RestAPI = RestAPI()) {

    fun getLessons(classRoom: Int,lang: String): Flowable<Response<List<Lesson>>>
        = RestAPI.getApi().getSubjects(classRoom,lang)

    fun getTeachers(classRoom: Int,language: String,subjectId: Int,topicId: Int): Flowable<Response<List<Teacher>>>
        = RestAPI.getApi().getTeachersByTopic(
                classRoom,
                language,
                subjectId,
                topicId
        )

    fun getTopics(subjectId: Int): Flowable<Response<List<TopicItem>>>
        = RestAPI.getApi().getTopicsBySubject(subjectId)

    fun postLessonRequest(teacherId: String, topicId: Int)
        = RestAPI.getApi().postLessonRequest(teacherId,topicId)

}