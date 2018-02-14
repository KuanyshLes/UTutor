package com.support.robigroup.ututor.ui.login

import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView

interface LoginRegistrationActivityMvpView: MvpView{

    fun openMainActivity()
    fun replaceLoginFragment()

}

interface LoginRegistrationActivityMvpPresenter<V: LoginRegistrationActivityMvpView> : MvpPresenter<V>{
    fun onViewInitialized()
}

interface LoginFragmentMvpView: MvpView{

}

interface LoginFragmentMvpPresenter<V: LoginFragmentMvpView>: MvpPresenter<V>{

}

