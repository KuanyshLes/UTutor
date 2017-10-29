package com.support.robigroup.ututor.ui.chat

import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView


interface ChatMvpView : MvpView {

    fun openMainActivity()
}


interface ChatMvpPresenter<V : ChatMvpView> : MvpPresenter<V> {

    fun onServerLoginClick(email: String, password: String)

    fun onGoogleLoginClick()

    fun onFacebookLoginClick()

}