package com.support.robigroup.ututor.commons

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.reactivex.disposables.CompositeDisposable

open class RxBaseFragment : Fragment() {

    protected var subscriptions = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        subscriptions = CompositeDisposable()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onDestroyView() {
        subscriptions.clear()
        super.onDestroyView()
    }

}