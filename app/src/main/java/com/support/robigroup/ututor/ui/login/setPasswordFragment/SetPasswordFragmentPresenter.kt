package com.support.robigroup.ututor.ui.login.setPasswordFragment

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.api.FakeServer
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.ui.login.RegPhoneNumberFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.RegPhoneNumberFragmentView
import com.support.robigroup.ututor.ui.login.SetPasswordFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.SetPasswordFragmentView
import com.support.robigroup.ututor.utils.CommonUtils
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import javax.inject.Inject


class SetPasswordFragmentPresenter<V : SetPasswordFragmentView>
@Inject
constructor(dataManager: DataManager,
            schedulerProvider: SchedulerProvider,
            compositeDisposable: CompositeDisposable)
    : BasePresenter<V>(dataManager, schedulerProvider, compositeDisposable),
        SetPasswordFragmentMvpPresenter<V> {

    override fun onSetPasswordButtonClicked(password: String, confirmPassword: String) {

        mvpView.resetErrors()

        if (password.isEmpty()) {
            mvpView.setPasswordError("")
            return
        }
        if (confirmPassword.isEmpty()) {
            mvpView.setConfirmPasswordError("")
            return
        }
        if (!CommonUtils.isPasswordValid(password)) {
            mvpView.setPasswordError(null)
            return
        }

        if (!CommonUtils.isPasswordValid(confirmPassword)) {
            mvpView.setConfirmPasswordError(null)
            return
        }
        if (password != confirmPassword) {
            mvpView.setUnmatchedPasswordsError()
            return
        }

        val phoneToken = sharedPreferences.getString(Constants.KEY_SAVE_PHONE_TOKEN)

        var remoteSource: Single<Response<ResponseBody>>? = null
        if(Constants.DEBUG){
            remoteSource = FakeServer.getFakeValidSetPasswordResponse()
        }else{
            remoteSource = dataManager.apiHelper.password(password, phoneToken)
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
                            if(body.getString(Constants.KEY_GET_TOKEN_FROM_RESULT_BODY).isEmpty()){
                                throw Exception("empty token exception")
                            }
                            val token = Constants.KEY_BEARER + body.getString(Constants.KEY_GET_TOKEN_FROM_RESULT_BODY)
                            sharedPreferences.put(Constants.KEY_TOKEN, token)
                            mvpView.openMenuActivity()
                        } catch (e: Exception) {
                            handleApiError(ANError(e))
                        }
                    } else {
                        try {
                            val value = response.errorBody()?.string()
                            mvpView.setConfirmPasswordError(value)
                        } catch (e: Exception) {
                            handleApiError(ANError(e))
                        }
                    }
                }, { error ->
                    handleApiError(ANError(error))
                })
        )


    }
}