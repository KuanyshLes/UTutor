package com.support.robigroup.ututor

import com.support.robigroup.ututor.commons.Language
import com.support.robigroup.ututor.commons.Type

object Constants {

    const val BASE_URL = "https://ututor.kz/"
    const val DEBUG = false
    const val DEBUG_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy54bWxzb2FwLm9yZy93cy8yMDA1LzA1L2lkZW50aXR5L2NsYWltcy9uYW1laWRlbnRpZmllciI6ImRmMWRjNTNkLWE4MTctNGY2NC04N2I2LTNmZTRmNzVmMWYxYiIsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWUiOiJiZXliaXQ5MkBnbWFpbC5jb20iLCJodHRwOi8vdXR1dG9yLmt6L2NsYWltcy9waG9uZWNvbmZpcm1lZCI6IlRydWUiLCJodHRwOi8vdXR1dG9yLmt6L2NsYWltcy9oYXNwYXNzd29yZCI6IlRydWUiLCJqdGkiOiIzODQ3ZjI0MC0xYjU4LTQ3NGEtYjFkNC00YWI1NjdjOWY1YjgiLCJpYXQiOjE1MTg5NDA4MDgsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvcm9sZSI6IkxlYXJuZXIiLCJuYmYiOjE1MTg5NDA4MDgsImV4cCI6MTUzMTkwMDgwOCwiaXNzIjoiVVR1dG9ySXNzdWVyIiwiYXVkIjoiVVR1dG9yQXVkaWVuY2UifQ.B7Q1IHKo-xjw0zLYlpKb2gk1rAKxOUnqk_yWbSdHW0o"

    const val BAD_REQUEST = 400 // плохой плохой клиент каку написал
    const val UNAUTHORIZED = 401 // нужен токен
    const val FORBIDDEN = 403 // запрещенный запрос
    const val NOT_FOUND = 404 // не найден, not our problem
    const val SERVER_ERROR = 500 //not our problem

    const val LEARNER_ID = "Learner"
    const val TEACHER_ID = "Teacher"

    const val KEY_TOKEN = "TOKEN"
    const val KEY_FULL_NAME = "full_name"
    const val KEY_LANGUAGE = "language"
    const val KEY_BALANCE = "balance"
    const val KEY_PROFILE = "profile"
    const val KEY_EMAIL = "username"
    const val KEY_SAVE_EMAIL_TOKEN = "username"
    const val KEY_SAVE_PHONE_TOKEN = "username"
    const val KEY_PHONE_NUMBER = "number"
    const val KEY_PASSWORD = "password"
    const val KEY_GET_TOKEN_FROM_RESULT_BODY = "access_token"
    const val KEY_RES_EXPIRES = "expires_in"
    const val KEY_BEARER = "bearer "

    //chat constants
    const val STATUS_NOT_REQUESTED = -3
    const val STATUS_COMPLETED = 6
    const val STATUS_CANCELLED = 6

    const val TAG_READY_DIALOG = "readyDialog"
    const val TAG_FINISH_DIALOG = "finishDialog"
    const val TAG_RATE_DIALOG = "evalDialog"

    //chat message status
    const val MESSAGE_PAUSE = 1
    const val MESSAGE_DOWNLOADING = 2
    const val MESSAGE_DOWNLOAD = 0
    const val MESSAGE_PLAYING = 3
    const val MESSAGE_STOPPED = 4


    const val DEVICE_TIMEFORMAT: String = "yyyy-MM-dd HH:mm:sss"
    const val BACKEND_TIMEFORMAT: String = "yyyy-MM-dd'T'HH:mm:ss"
    const val WAIT_TIME = 30000

    const val CONTENT_TYPE_IMAGE_TEXT: Byte = 100
    const val CONTENT_TYPE_VOICE: Byte = 101

    const val PASSWORD_PATTERN = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$"
    const val EMAIL_PATTERN = "^[a-zA-Z0-9#_~!$&'()*+,;=:.\"(),:;<>@\\[\\]\\\\]+@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*$"
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
