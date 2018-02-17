package com.support.robigroup.ututor.ui.login

import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView

interface LoginRegistrationActivityMvpView: MvpView{

    fun openMainActivity()
    fun replaceLoginFragment()
    fun replaceRegistrationFragment()

}

interface LoginRegistrationActivityMvpPresenter<V: LoginRegistrationActivityMvpView> : MvpPresenter<V>{
    fun onViewInitialized()
}

interface LoginFragmentMvpView: MvpView{

    fun setIncorrectLoginError(error: String)
    fun setIncorrectPasswordError(error: String)
    fun resetErrors()
    fun openRegistrationFragment()

}

interface LoginFragmentMvpPresenter<V: LoginFragmentMvpView>: MvpPresenter<V>{

    fun onSignUpButtonClicked()
    fun onSignInButtonClicked(email: String, password: String)

}

