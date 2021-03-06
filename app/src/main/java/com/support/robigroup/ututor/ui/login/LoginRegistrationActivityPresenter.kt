package com.support.robigroup.ututor.ui.login

import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.data.DataManager
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

    override fun onViewInitialized() {
        if(isSignedIn()){
            mvpView.replaceLoginFragment()
        }else{
            mvpView.startMenuActivity()
        }
    }

    private fun isSignedIn(): Boolean{
        return sharedPreferences.getString(Constants.KEY_TOKEN,"") == ""
    }
}
