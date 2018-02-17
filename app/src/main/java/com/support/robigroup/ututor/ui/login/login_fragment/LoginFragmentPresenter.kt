package com.support.robigroup.ututor.ui.login.login_fragment

import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpView
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginFragmentPresenter<V : LoginFragmentMvpView>
@Inject
constructor(
        dataManager: DataManager,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable
): BasePresenter<V>(dataManager, schedulerProvider, compositeDisposable),
        LoginFragmentMvpPresenter<V> {

    override fun onSignUpButtonClicked() {

    }

    override fun onSignInButtonClicked(email: String, password: String) {

    }
}
