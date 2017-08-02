package com.support.robigroup.ututor.screen.login

/**
 * Created by Bimurat Mukhtar on 02.08.2017.
 */
interface OnLoginActivityInterationListener {
    fun OnSignInButtonClicked (email: String,password: String)
    fun OnSignUpTextClicked ()
    fun OnNextButtonClicked (email: String,password: String,phone: String)
    fun OnDoneButtonClicked (firstName: String,lastName: String)
    fun OnUploadPhotoClicked ()
}