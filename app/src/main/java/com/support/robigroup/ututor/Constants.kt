package com.support.robigroup.ututor

import com.support.robigroup.ututor.commons.Language
import com.support.robigroup.ututor.commons.Type

object Constants {

    val DEBUG = false
    val DEBUG_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6ImRmMWRjNTNkLWE4MTctNGY2NC04N2I2LTNmZTRmNzVmMWYxYiIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJiZXliaXQ5MkBnbWFpbC5jb20iLCJodHRwOi8vdXR1dG9yLmt6L2NsYWltcy9waG9uZWNvbmZpcm1lZCI6IlRydWUiLCJodHRwOi8vdXR1dG9yLmt6L2NsYWltcy9oYXNwYXNzd29yZCI6IlRydWUiLCJqdGkiOiIzODQ3ZjI0MC0xYjU4LTQ3NGEtYjFkNC00YWI1NjdjOWY1YjgiLCJpYXQiOjE1MTg5NDA4MDgsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6IkxlYXJuZXIiLCJuYmYiOjE1MTg5NDA4MDgsImV4cCI6MTUzMTkwMDgwOCwiaXNzIjoiVVR1dG9ySXNzdWVyIiwiYXVkIjoiVVR1dG9yQXVkaWVuY2UifQ.B7Q1IHKo-xjw0zLYlpKb2gk1rAKxOUnqk_yWbSdHW0o"

    val BAD_REQUEST = 400 // плохой плохой клиент каку написал
    val UNAUTHORIZED = 401 // нужен токен
    val FORBIDDEN = 403 // запрещенный запрос
    val NOT_FOUND = 404 // не найден, not our problem
    val SERVER_ERROR = 500 //not our problem

    val LEARNER_ID = "Learner"
    val TEACHER_ID = "Teacher"

    val KEY_TOKEN = "TOKEN"
    val KEY_FULL_NAME = "full_name"
    val KEY_LANGUAGE = "language"
    val KEY_BALANCE = "balance"
    val KEY_PROFILE = "profile"
    val KEY_EMAIL = "username"
    val KEY_SAVE_EMAIL_TOKEN = "username"
    val KEY_SAVE_PHONE_TOKEN = "username"
    val KEY_PHONE_NUMBER = "number"
    val KEY_PASSWORD = "password"
    val KEY_GET_TOKEN_FROM_RESULT_BODY = "access_token"
    val KEY_RES_EXPIRES = "expires_in"
    val KEY_BEARER = "bearer "

    val BASE_URL = "https://ututor.kz/"

    //chat constants
    val STATUS_NOT_REQUESTED = -3
    val STATUS_COMPLETED = 6
    val STATUS_CANCELLED = 6

    val TAG_READY_DIALOG = "readyDialog"
    val TAG_FINISH_DIALOG = "finishDialog"
    val TAG_RATE_DIALOG = "evalDialog"

    val MESSAGE_PAUSE = 1
    val MESSAGE_DOWNLOADING = 2
    val MESSAGE_DOWNLOAD = 0
    val MESSAGE_PLAYING = 3
    val MESSAGE_STOPPED = 4


    val DEVICE_TIMEFORMAT: String = "yyyy-MM-dd HH:mm:sss"
    val BACKEND_TIMEFORMAT: String = "yyyy-MM-dd'T'HH:mm:ss"
    val WAIT_TIME = 30000

    val CONTENT_TYPE_IMAGE_TEXT: Byte = 100
    val CONTENT_TYPE_VOICE: Byte = 101

    val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"
    val FLAGS = arrayOf(
            Language(R.drawable.flag_kz, "Казахский", "kk", false),
            Language(R.drawable.flag_ru, "Русский", "ru", false),
            Language(R.drawable.flag_us, "Английский", "en", false)
    )

    val TYPES = arrayOf(
            Type(1, "Домашнее задание", R.drawable.ic_choose_homes),
            Type(2, "Вопросы по тесту", R.drawable.ic_choose_test),
            Type( 3,"Подготовка к ЕНТ", R.drawable.ic_choose_test)
    )

    val AUDIO_TYPES = arrayOf(
            "mp3",
            "3gp",
            "mp4",
            "wav"
    )
    val IMAGE_TYPES = arrayOf(
            "jpg",
            "png",
            "bmp",
            "gif"
    )



}
