package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.model.content.Lesson
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.Flowable
import io.reactivex.Observable
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*
import java.util.HashMap

interface APIInterface {
    @GET("/top.json")
    fun getTop(@Query("after") after: String,
               @Query("limit") limit: String): Call<RedditNewsResponse>

    @POST("token")
    fun getToken(
            @Body data: HashMap<String,String>
    ): Flowable<Response<String>>

    @GET("api/sprs/topics")
    fun getTopicsBySubject(
            @Query("subject") subject: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<TopicItem>>>

    @GET("api/sprs/subjects")
    fun getSubjects(
            @Query("class") classRoom: Int,
            @Query("lang")lang : String,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<Lesson>>>

    @POST("api/learner/search/teachers")
    fun getTeachersByTopic(
            @Query("class") classRoom: Int,
            @Query("Language") language: String,
            @Query("SubjectId") subjectId: Int,
            @Query("TopicId") topicId: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<Teacher>>>

    @POST("api/learner/lesson/request")
    fun postLessonRequest(
            @Query("TeacherId") teacherId: String,
            @Query("TopicId") topicId: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    )

}