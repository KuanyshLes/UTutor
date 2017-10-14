package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.features.chat.model.CustomMessage
import com.support.robigroup.ututor.features.chat.model.CustomMessageHistory
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.Flowable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface APIInterface {

    @FormUrlEncoded
    @POST("token")
    fun getToken(
            @Field("username") username: String,
            @Field("password") password: String
    ): Flowable<Response<LoginResponse>>

    @GET("api/sprs/topics")
    fun getTopicsBySubject(
            @Query("subject") subject: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<TopicItem>>>

    @GET("api/sprs/subjects")
    fun getSubjects(
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<Subject>>>

    @GET("api/account/profile/learner")
    fun getBalance(
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<Profile>>

    @POST("api/learner/search/teachers")
    fun getTeachers(
            @Query("Class") classRoom: Int,
            @Query("Language") language: String,
            @Query("SubjectId") subjectId: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<Teacher>>>

    @POST("api/learner/lesson/request")
    fun postLessonRequest(
            @Query("TeacherId") teacherId: String,
            @Query("SubjectId") subject: Int,
            @Query("Language") language: String,
            @Query("Class") classNumber: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<LessonRequestForTeacher>>

    @POST("api/learner/lesson/request/cancel")
    fun postCancelRequest(
            @Query("id") requestId: String,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ResponseBody>>

    @GET("api/lesson/chat")
    fun getInformationAboutChat(
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ChatLesson>>

    @GET("api/learner/lesson/history")
    fun getHistory(
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<ChatHistory>>>

    @GET("api/lesson/chat/messages/{id}")
    fun getHistoryMessages(
            @Path("id") chatId: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<CustomMessageHistory>>>

    @GET("api/lesson/chat/messages")
    fun getChatMeassages(
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ChatLesson>>

    @GET("Lesson/ReadyForChat")  //   api/lesson/chat/ready
    fun postChatReady(
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ResponseBody>>

    @GET("api/lesson/chat/complete")
    fun postChatComplete(
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ChatLesson>>

    @POST("api/lesson/chat/message/file")
    fun postMessagePhoto(
            @Body hashMap: HashMap<String,String>,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<CustomMessage>>

    @POST("api/lesson/chat/message")
    fun postTextMessage(
            @Query("message") messageText: String,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<CustomMessage>>

    @GET("api/lesson/rate")
    fun evalChat(
            @Query("LessonId") rating: Int,
            @Query("Raiting") lessonId: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ResponseBody>>

    @POST("api/account/password/change")
    fun resetPassword(
            @Query("OldPassword") oldPassword: String,
            @Query("NewPassword") newPassword: String,
            @Query("ConfirmPassword") confirmPassword: String,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ResponseBody>>
}