package com.support.robigroup.ututor.ui.navigationDrawer.account

import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView

interface AccountFragmentMvpView: MvpView {
    fun openChat()
}

interface AccountFragmentMvpPresenter<V : AccountFragmentMvpView> : MvpPresenter<V>{
    fun onViewInitialized()
}