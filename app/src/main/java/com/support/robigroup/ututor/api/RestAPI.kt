package com.support.robigroup.ututor.api

import com.androidnetworking.interceptors.HttpLoggingInterceptor
import com.support.robigroup.ututor.Constants
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import okhttp3.Response
import java.io.IOException
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit


class RestAPI{

    companion object {

        private val logging = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        private var client = OkHttpClient.Builder()
                .readTimeout(10, TimeUnit.SECONDS)
                .connectTimeout(5, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val newRequest = chain.request().newBuilder()

                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build()
                return chain.proceed(newRequest)
            }
        }).build()

        private var uploadImage = OkHttpClient.Builder()
                .readTimeout(10000, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor { chain: Interceptor.Chain ->
                    val newRequest = chain.request().newBuilder()
                            .addHeader("Content-Type", "application/x-www-form-urlencoded")
                            .build()
                    chain.proceed(newRequest)
                }
                .build()

        private val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(
                        GsonBuilder()
                        .setLenient()
                        .create()
                ))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        private val uploadImageRetrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .client(uploadImage)
                .addConverterFactory(GsonConverterFactory.create(
                        GsonBuilder()
                                .setLenient()
                                .create()
                ))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        private val apiInterface: APIInterface = retrofit.create(APIInterface::class.java)
        private val uploadApiInterface: APIInterface = uploadImageRetrofit.create(APIInterface::class.java)

        fun getUploadApi(): APIInterface =  uploadApiInterface
        fun getApi(): APIInterface = apiInterface
    }


}
