package com.support.robigroup.ututor.commons

import android.support.v4.app.Fragment
import io.reactivex.disposables.CompositeDisposable

open class RxBaseFragment : Fragment() {

    protected var subscriptions = CompositeDisposable()

    override fun onResume() {
        super.onResume()
        logd("onResumeRxBaseFragment")
        subscriptions = CompositeDisposable()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
        logd("onPauseRxBaseFragment")
    }
}