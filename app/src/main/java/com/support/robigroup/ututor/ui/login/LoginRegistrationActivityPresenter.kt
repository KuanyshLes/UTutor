package com.support.robigroup.ututor.ui.login

import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class LoginRegistrationActivityPresenter<V : LoginRegistrationActivityMvpView> @Inject
constructor(
        dataManager: DataManager,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable
):BasePresenter<V>(dataManager, schedulerProvider, compositeDisposable),
        LoginRegistrationActivityMvpPresenter<V>{

    private lateinit var sharedPref: SingletonSharedPref

    override fun onViewInitialized() {
        sharedPref = SingletonSharedPref.getInstance()

        if(isSignedIn()){
            mvpView.openMainActivity()
        }else{
            mvpView.replaceLoginFragment()
        }
    }

    private fun isSignedIn(): Boolean{
        return sharedPref.getString(Constants.KEY_TOKEN,"") == ""
    }
}
