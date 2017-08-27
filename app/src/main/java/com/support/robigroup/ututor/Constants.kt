package com.support.robigroup.ututor

object Constants {

    val BAD_REQUEST = 400 // плохой плохой клиент каку написал
    val UNAUTHORIZED = 401 // нужен токен
    val FORBIDDEN = 403 // запрещенный запрос
    val NOT_FOUND = 404 // не найден, not our problem
    val SERVER_ERROR = 500 //not our problem



    val KEY_TOKEN = "TOKEN"
    val KEY_EMAIL = "username"
    val KEY_PASSWORD = "password"
    val KEY_RES_TOKEN = "access_token"
    val KEY_RES_EXPIRES = "expires_in"
    val KEY_BEARER = "bearer "

    val BASE_URL = "http://ututor.azurewebsites.net/"

    //chat constants
    val STATUS_NOT_REQUESTED = 0
    val STATUS_REQUESTED = 1
    val STATUS_ACCEPTED = 2
    val STATUS_LEARNER_CONFIRMED = 3
    val STATUS_TEACHER_CONFIRMED = 4
    val STATUS_DECLINED = 5


}
