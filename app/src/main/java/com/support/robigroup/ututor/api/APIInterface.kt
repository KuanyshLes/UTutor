package com.support.robigroup.ututor.api

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


}