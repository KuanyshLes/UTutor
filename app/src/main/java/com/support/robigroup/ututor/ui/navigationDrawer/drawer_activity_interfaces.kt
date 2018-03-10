package com.support.robigroup.ututor.ui.navigationDrawer

import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView


interface DrawerMvpView: MvpView {
    fun updateProfile()
    fun updateLanguageAndFlag()
    fun setActionBarTitle(title: String)
    fun stopBackgroundService()
    fun openLoginRegistrationActivity()
}

interface DrawerMvpPresenter<V : DrawerMvpView> : MvpPresenter<V>{
    fun onViewInitialized()
    fun onLogoutClicked()
}
