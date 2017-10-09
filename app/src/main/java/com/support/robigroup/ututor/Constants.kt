package com.support.robigroup.ututor

import com.support.robigroup.ututor.model.content.Language
import java.util.*

object Constants {

    val BAD_REQUEST = 400 // плохой плохой клиент каку написал
    val UNAUTHORIZED = 401 // нужен токен
    val FORBIDDEN = 403 // запрещенный запрос
    val NOT_FOUND = 404 // не найден, not our problem
    val SERVER_ERROR = 500 //not our problem

    val KEY_TOKEN = "TOKEN"
    val KEY_FULL_NAME = "full_name"
    val KEY_LANGUAGE = "language"
    val KEY_BALANCE = "balance"
    val KEY_EMAIL = "username"
    val KEY_PASSWORD = "password"
    val KEY_RES_TOKEN = "access_token"
    val KEY_RES_EXPIRES = "expires_in"
    val KEY_BEARER = "bearer "

    val BASE_URL = "http://ututor.kz/"

    //chat constants
    val STATUS_REQUESTED_WAIT = -2
    val STATUS_ACCEPTED_TEACHER = -1
    val STATUS_NOT_REQUESTED = -3
    val STATUS_DECLINED = 5
    val STATUS_ERROR = -4
    val STATUS_COMPLETED = 6

    val UTC: TimeZone = TimeZone.getTimeZone("UTC")
    val TIMEFORMAT: String = "yyyy-MM-dd'T'HH:mm:ss"
    val WAIT_TIME = 30000
    val UTC_TIME = 6*60*60*1000

    val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"
    val FLAGS = arrayOf(
            Language(R.drawable.flag_kz,"Қазақ","kk",false),
            Language(R.drawable.flag_ru,"Русский","ru",false),
            Language(R.drawable.flag_us,"English","en",false)
    )



}
