package com.support.robigroup.ututor.ui.chat

import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView


interface ChatMvpView : MvpView {

    fun openMenuActivity()

    fun showFinishDialog()

    fun closeFinishDialog()

    fun showReadyDialog(dif: Long)

    fun closeReadyDialog()

    fun onLearnerReadyDialog()

    fun showEvalDialog()

    fun closeEvalDialog()

    fun changeCounterValueText(text: String)

}


interface ChatMvpPresenter<V : ChatMvpView> : MvpPresenter<V> {

    fun onFinishClick()

    fun onReadyClick()

    fun onViewInitialized()

    fun onDestroyReadyView()

    fun onCounterFinish()
}