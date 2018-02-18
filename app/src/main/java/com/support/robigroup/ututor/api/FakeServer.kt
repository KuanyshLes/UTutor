package com.support.robigroup.ututor.api

import com.support.robigroup.ututor.Constants
import io.reactivex.Single
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import java.net.HttpURLConnection


class FakeServer{

    companion object {

        fun getFakeValidVerifyCodeResponse(): Single<Response<ResponseBody>>{
            val jsonObject = JSONObject()
            jsonObject.put(Constants.KEY_GET_TOKEN_FROM_RESULT_BODY, Constants.DEBUG_TOKEN)

            val response: Response<ResponseBody> = Response.success(
                    ResponseBody.create(MediaType.parse("text/plain"), jsonObject.toString()))
            val single: Single<Response<ResponseBody>> = Single.just(response)
            return single
        }
        fun getFakeInvalidVerifyCodeResponse(): Single<Response<ResponseBody>>{
            val response: Response<ResponseBody> = Response.error(
                    HttpURLConnection.HTTP_BAD_REQUEST,
                    ResponseBody.create(MediaType.parse("text/plain"), "invalid password"))
            val single: Single<Response<ResponseBody>> = Single.just(response)
            return single
        }
        fun getFakePhoneNumberResponse(): Single<Response<ResponseBody>>{
            val response: Response<ResponseBody> = Response.success(
                    ResponseBody.create(MediaType.parse("text/plain"), ""))
            val single: Single<Response<ResponseBody>> = Single.just(response)
            return single
        }
        fun getFakeValidSetPasswordResponse(): Single<Response<ResponseBody>>{
            val jsonObject = JSONObject()
            jsonObject.put(Constants.KEY_GET_TOKEN_FROM_RESULT_BODY, Constants.DEBUG_TOKEN)
            val response: Response<ResponseBody> = Response.success(
                    ResponseBody.create(MediaType.parse("text/plain"), jsonObject.toString()))
            val single: Single<Response<ResponseBody>> = Single.just(response)
            return single
        }
    }


}
