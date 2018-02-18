package com.support.robigroup.ututor.ui.login.regPhoneNumberFragment

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.api.FakeServer
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.ui.login.RegPhoneNumberFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.RegPhoneNumberFragmentView
import com.support.robigroup.ututor.utils.CommonUtils
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class RegPhoneNumberFragmentPresenter<V : RegPhoneNumberFragmentView>
@Inject
constructor(dataManager: DataManager,
            schedulerProvider: SchedulerProvider,
            compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable),
        RegPhoneNumberFragmentMvpPresenter<V> {

    override fun onRegisterPhoneButtonClicked(number: String) {

        mvpView.resetErrors()

        if (number.isEmpty()) {
            mvpView.setEmptyNumberError()
            return
        }
        if (!CommonUtils.isPhoneNumberValid(number)) {
            mvpView.setIncorrectNumberError(null)
            return
        }

        val emailToken = sharedPreferences.getString(Constants.KEY_SAVE_EMAIL_TOKEN)

        var remoteSource: Single<Response<ResponseBody>>? = null
        if(Constants.DEBUG){
            remoteSource = FakeServer.getFakePhoneNumberResponse()
        }else{
            remoteSource = dataManager.apiHelper.getPhone(number, emailToken)
        }

        compositeDisposable.add(remoteSource
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe {
                    mvpView.showLoading()
                }
                .doAfterTerminate {
                    mvpView.hideLoading()
                }
                .subscribe ({
                    response ->
                    if (response.isSuccessful) {
                        sharedPreferences.put(Constants.KEY_PHONE_NUMBER, number)
                        mvpView.openVerifyCodeFragment()
                    } else {
                        try {
                            val body = JSONObject(response.errorBody()?.string())
                            val keys = body.keys()
                            while (keys.hasNext()) {
                                val key = keys.next() as String
                                val value = body.getString(key)
                                when(key){
                                    "PhoneNumber" -> mvpView.setIncorrectNumberError(value)
                                    "" -> handleApiError(ANError(value))
                                }
                            }
                        } catch (e: Exception) {
                            handleApiError(ANError(e))
                        }
                    }
                },{
                    error -> handleApiError(ANError(error))
                })
        )

    }
}