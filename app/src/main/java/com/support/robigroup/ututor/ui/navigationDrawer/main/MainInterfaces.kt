package com.support.robigroup.ututor.ui.navigationDrawer.main

import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView

interface MainFragmentMvpView: MvpView {
    fun openChat()
}

interface MainFragmentMvpPresenter<V : MainFragmentMvpView> : MvpPresenter<V>{
    fun onViewInitialized()
}