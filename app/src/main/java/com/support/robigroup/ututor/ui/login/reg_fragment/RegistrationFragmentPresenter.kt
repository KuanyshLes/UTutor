package com.support.robigroup.ututor.ui.login.reg_fragment

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.ui.login.RegistrationFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.RegistrationFragmentView
import com.support.robigroup.ututor.utils.CommonUtils
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import org.json.JSONObject
import javax.inject.Inject


class RegistrationFragmentPresenter<V : RegistrationFragmentView>
@Inject
constructor(dataManager: DataManager,
            schedulerProvider: SchedulerProvider,
            compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable),
        RegistrationFragmentMvpPresenter<V> {

    override fun onRegisterEmailButtonClicked(name: String, surname: String, email: String) {

        mvpView.resetErrors()

        if (surname.isEmpty()) {
            mvpView.setEmptySurnameError()
            return
        }
        if (name.isEmpty()) {
            mvpView.setEmptyNameError()
            return
        }
        if (!CommonUtils.isEmailValid(email)) {
            mvpView.setIncorrectEmailError(null)
            return
        }

        compositeDisposable.add(dataManager.apiHelper.register(name, surname, email)
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
                            sharedPreferences.put(Constants.KEY_FULL_NAME, name + surname)
                            val token = Constants.KEY_BEARER + body.getString(Constants.KEY_GET_TOKEN_FROM_RESULT_BODY)
                            sharedPreferences.put(Constants.KEY_SAVE_EMAIL_TOKEN, token)
                            mvpView.openRegPhoneNumberFragment()
                        } catch (e: Exception) {
                            handleApiError(ANError(e))
                        }
                    } else {
                        try {
                            val body = JSONObject(response.errorBody()?.string())
                            val keys = body.keys()
                            while (keys.hasNext()) {
                                val key = keys.next() as String
                                val value = body.getString(key)
                                when (key) {
                                    "Email" -> mvpView.setIncorrectEmailError(value)
                                    "" -> handleApiError(ANError(value))
                                    "FirstName" -> mvpView.setNameError(value)
                                    "LastName" -> mvpView.setSurnameError(value)
                                }
                            }
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