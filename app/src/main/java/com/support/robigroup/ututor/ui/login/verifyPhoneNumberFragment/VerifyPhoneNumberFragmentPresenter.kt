package com.support.robigroup.ututor.ui.login.regFragment

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.api.FakeServer
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.ui.login.VerifyPhoneNumberFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.VerifyPhoneNumberFragmentView
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class VerifyPhoneNumberFragmentPresenter<V : VerifyPhoneNumberFragmentView>
@Inject
constructor(dataManager: DataManager,
            schedulerProvider: SchedulerProvider,
            compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable),
        VerifyPhoneNumberFragmentMvpPresenter<V> {



    override fun onVerifyPhoneNumberButtonClicked(code: String) {

        mvpView.resetErrors()

        if (code.isEmpty()) {
            mvpView.setCodeError("")
            return
        }

        val phoneNumber = sharedPreferences.getString(Constants.KEY_PHONE_NUMBER)
        val emailToken = sharedPreferences.getString(Constants.KEY_SAVE_EMAIL_TOKEN)

        var remoteSource: Single<Response<ResponseBody>>? = null
        if(Constants.DEBUG){
            remoteSource = FakeServer.getFakeValidVerifyCodeResponse()
        }else{
            remoteSource = dataManager.apiHelper.verifyPhoneNumberWithCode(code, phoneNumber, emailToken)
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
                .subscribe({ response ->

                    if (response.isSuccessful) {
                        try {
                            val body = JSONObject(response.body()?.string())
                            val token = Constants.KEY_BEARER + body.getString(Constants.KEY_GET_TOKEN_FROM_RESULT_BODY)
                            sharedPreferences.put(Constants.KEY_SAVE_PHONE_TOKEN, token)
                            mvpView.replaceSetPasswordFragment()
                        } catch (e: Exception) {
                            handleApiError(ANError(e))
                        }
                    } else {
                        val value = response.errorBody()?.string()
                        mvpView.setCodeError(value)
                    }
                }, { error -> handleApiError(ANError(error)) }
                )
        )

    }
}