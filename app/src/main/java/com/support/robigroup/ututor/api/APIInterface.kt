package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.model.content.Lesson
import com.support.robigroup.ututor.model.content.TopicItem
import io.reactivex.Observable
import okhttp3.RequestBody
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
    ): Observable<Response<String>>

    @GET("api/sprs/topics")
    fun getTopicsBySubject(@Query("subject") subject: Int = 5): Observable<Response<List<TopicItem>>>

    @GET("api/sprs/subjects")
    fun getSubjects(@Query("class") classRoom: Int = 10, @Query("lang")lang : String = "kk-KZ"): Observable<Response<List<Lesson>>>




}