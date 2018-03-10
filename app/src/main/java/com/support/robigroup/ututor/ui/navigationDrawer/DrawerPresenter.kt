package com.support.robigroup.ututor.ui.navigationDrawer

import com.androidnetworking.error.ANError
import com.google.gson.Gson
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.Profile
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class DrawerPresenter<V : DrawerMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
: BasePresenter<V>(dataManager, schedulerProvider, compositeDisposable), DrawerMvpPresenter<V> {

    override fun onViewInitialized() {
        requestProfile()
    }

    override fun onLogoutClicked() {
        sharedPreferences.clear()
        mvpView.stopBackgroundService()
        mvpView.openLoginRegistrationActivity()
    }

    private fun requestProfile(){
        compositeDisposable.add(dataManager.apiHelper.getBalance()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { retrievedLessons ->
                            if(retrievedLessons.isSuccessful){
                                if(retrievedLessons.body()!=null){
                                    val profile = retrievedLessons.body()!!
                                    val myBal = profile.Balance
                                    sharedPreferences.put(Constants.KEY_BALANCE,myBal ?: 0.0)
                                    sharedPreferences
                                            .put(Constants.KEY_PROFILE, Gson().toJson(profile, Profile::class.java))
                                    mvpView.updateProfile()
                                    mvpView.updateLanguageAndFlag()
                                }
                            }else{
                                handleApiError(ANError(retrievedLessons.raw()))
                            }
                        },
                        { e ->
                            handleApiError(ANError(e))
                        }
                ))
    }
}
