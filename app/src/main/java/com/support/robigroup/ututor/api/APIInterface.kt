package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.features.chat.model.ChatMessage
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

    @GET("api/sprs/lesson/types")
    fun getTypes(
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<List<Type>>>


    @GET("api/sprs/subjects")
    fun getSubjects(
            @Query("type") type: Int,
//            @Query("class") classNumber: Int,
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
            @Query("LessonTypeId") type: Int,
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
    ): Flowable<Response<List<ChatMessage>>>

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
    ): Flowable<Response<ChatMessage>>

    @POST("api/lesson/chat/message")
    fun postTextMessage(
            @Query("message") messageText: String,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ChatMessage>>

    @GET("api/lesson/rate")
    fun evalChat(
            @Query("LessonId") rating: Int,
            @Query("Raiting") lessonId: Int,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ResponseBody>>

    @POST("api/account/review")
    fun postFeedback(
            @Query("Text") text: String,
            @Query("AppVersion") appVersion: String,
            @Query("Device") device: String,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ResponseBody>>

    @POST("api/account/password/change")
    fun resetPassword(
            @Query("OldPassword") oldPassword: String,
            @Query("NewPassword") newPassword: String,
            @Query("ConfirmPassword") confirmPassword: String,
            @Header("Authorization") header: String = SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN)
    ): Flowable<Response<ResponseBody>>


    //REGISTER
    @POST("api/account/register")
    fun register(
            @Query("FirstName") firstName: String,
            @Query("LastName") lastName: String,
            @Query("Email") email: String,
            @Query("RoleId") roleId: String = "Learner"
    ): Flowable<Response<ResponseBody>>

    @GET("api/account/phone/verify")
    fun getPhone(
            @Query("PhoneNumber") phoneNumber: String,
            @Header("Authorization") token: String
    ): Flowable<Response<ResponseBody>>

    @POST("api/account/phone/verify")
    fun postPhone(
            @Query("Code") code: String,
            @Query("PhoneNumber") phoneNumber: String,
            @Header("Authorization") token: String
    ): Flowable<Response<ResponseBody>>

    @POST("api/account/password")
    fun password(
            @Query("Password") password: String,
            @Header("Authorization") token: String
    ): Flowable<Response<ResponseBody>>
}