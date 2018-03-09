package com.support.robigroup.ututor.ui.login.loginFragment

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.LoginResponse
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpView
import com.support.robigroup.ututor.utils.CommonUtils
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection
import javax.inject.Inject

class LoginFragmentPresenter<V : LoginFragmentMvpView>
@Inject
constructor(
        dataManager: DataManager,
        schedulerProvider: SchedulerProvider,
        compositeDisposable: CompositeDisposable
) : BasePresenter<V>(dataManager, schedulerProvider, compositeDisposable),
        LoginFragmentMvpPresenter<V> {

    override fun onSignUpButtonClicked() {
        mvpView.openRegistrationFragment()
    }

    override fun onSignInButtonClicked(email: String, password: String) {
        mvpView.resetErrors()

        if (!CommonUtils.isEmailValid(email)) {
            mvpView.setIncorrectEmailError(null)
            return
        }

        if (password.isEmpty()) {
            mvpView.setIncorrectPasswordError(null)
            return
        }
        compositeDisposable.add(dataManager.apiHelper.getToken(email, password)
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .doOnSubscribe {
                    mvpView.showLoading()
                }
                .doAfterTerminate {
                    mvpView.hideLoading()
                }
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            if (body.Role == Constants.TEACHER_ID) {
                                mvpView.showTeacherAccountError()
                            } else {
                                saveTokenAndFinish(body)
                            }
                        } else {
                            handleApiError(ANError())
                        }
                    } else {
                        when (response.code()) {
                            HttpURLConnection.HTTP_BAD_REQUEST -> mvpView.setIncorrectPasswordOrEmailError()
                            else -> handleApiError(ANError(response.raw()))
                        }
                    }
                }, { error ->
                    handleApiError(ANError(error))
                }
                )
        )
    }

    private fun saveTokenAndFinish(response: LoginResponse) {
        if(response.access_token.isEmpty()){
            handleApiError(ANError())
        }else{
            sharedPreferences.put(Constants.KEY_TOKEN, Constants.KEY_BEARER.plus(response.access_token))
            sharedPreferences.put(Constants.KEY_FULL_NAME, response.FullName)
            sharedPreferences.put(Constants.KEY_LANGUAGE, "kk")
            mvpView.startMenuActivity()
        }

    }

}
