package com.support.robigroup.ututor.ui.navigationDrawer.feedback

import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView

interface FeedbackFragmentMvpView : MvpView {
    fun onFeedbackSend()
    fun getDeviceName(): String
    fun getVersionCode(): String
    fun getVersionName(): String
    fun setDescriptionError(error: String?)
}

interface FeedbackFragmentMvpPresenter<V : FeedbackFragmentMvpView> : MvpPresenter<V> {
    fun onClickSend(text: String)
}
