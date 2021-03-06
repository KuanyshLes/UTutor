package com.support.robigroup.ututor.ui.login

import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView

interface LoginRegistrationActivityMvpView: MvpView{

    fun startMenuActivity()
    fun replaceLoginFragment()
    fun replaceRegistrationFragment()
    fun replaceRegPhoneNumberFragment()
    fun replaceVerifyPhoneNumberFragment()
    fun replaceSetPasswordFragment()

}

interface LoginRegistrationActivityMvpPresenter<V: LoginRegistrationActivityMvpView> : MvpPresenter<V>{
    fun onViewInitialized()
}

interface LoginFragmentMvpView: MvpView{

    fun setIncorrectEmailError(error: String?)
    fun setIncorrectPasswordError(error: String?)
    fun setIncorrectPasswordOrEmailError()
    fun showTeacherAccountError()
    fun resetErrors()
    fun startMenuActivity()
    fun openRegistrationFragment()

}

interface LoginFragmentMvpPresenter<V: LoginFragmentMvpView>: MvpPresenter<V>{

    fun onSignUpButtonClicked()
    fun onSignInButtonClicked(email: String, password: String)

}

interface RegistrationFragmentView: MvpView{

    fun resetErrors()
    fun setIncorrectEmailError(error: String?)
    fun setEmptyNameError()
    fun setEmptySurnameError()
    fun setSurnameError(error: String)
    fun setNameError(error: String)
    fun openRegPhoneNumberFragment()

}

interface RegistrationFragmentMvpPresenter<V: RegistrationFragmentView>: MvpPresenter<V>{

    fun onRegisterEmailButtonClicked(name: String, surname: String, email: String)

}

interface RegPhoneNumberFragmentView: MvpView{

    fun resetErrors()
    fun setIncorrectNumberError(error: String?)
    fun setEmptyNumberError()
    fun openVerifyCodeFragment()

}

interface RegPhoneNumberFragmentMvpPresenter<V: RegPhoneNumberFragmentView>: MvpPresenter<V>{

    fun onRegisterPhoneButtonClicked(number: String)

}

interface VerifyPhoneNumberFragmentView: MvpView{

    fun resetErrors()
    fun setCodeError(error: String?)
    fun replaceSetPasswordFragment()

}

interface VerifyPhoneNumberFragmentMvpPresenter<V: VerifyPhoneNumberFragmentView>: MvpPresenter<V>{

    fun onVerifyPhoneNumberButtonClicked(code: String)

}


interface SetPasswordFragmentView: MvpView{

    fun resetErrors()
    fun setPasswordError(error: String?)
    fun setConfirmPasswordError(error: String?)
    fun setUnmatchedPasswordsError()
    fun openMenuActivity()

}

interface SetPasswordFragmentMvpPresenter<V: SetPasswordFragmentView>: MvpPresenter<V>{

    fun onSetPasswordButtonClicked(password: String, confirmPassword: String)

}

