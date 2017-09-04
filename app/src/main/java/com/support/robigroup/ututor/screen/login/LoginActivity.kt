package com.support.robigroup.ututor.screen.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.OnLoginActivityInteractionListener
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.commons.toast
import com.support.robigroup.ututor.model.content.LoginResponse
import com.support.robigroup.ututor.screen.loading.LoadingDialog
import com.support.robigroup.ututor.screen.loading.LoadingView
import com.support.robigroup.ututor.screen.main.MainActivity
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginActivity : AppCompatActivity(), OnLoginActivityInteractionListener {

    val regFragment: RegistrationFragment = RegistrationFragment()
    val reg2Fragment: RegFragment2 = RegFragment2()
    val loginFragment: LoginFragment = LoginFragment()
    var loadingView: LoadingView = LoadingDialog.view(supportFragmentManager)
    val TAG_LOGIN_FRAGMENT: String = "loginFragment"

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private val mockServerResponse: String = "{ \"access_token\": \"bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiZXliaXQ5MkBnbWFpbC5jb20iLCJqdGkiOiJjYWJkODAxYi0xYjY1LTQ3NjEtOWQyYi0xY2Q1YzQ1MzA4ZGYiLCJpYXQiOjE1MDI1NDE1NjMsIm5iZiI6MTUwMjU0MTU2MywiZXhwIjoxNTAzMTQ2MzYzLCJpc3MiOiJVVHV0b3JJc3N1ZXIiLCJhdWQiOiJVVHV0b3JBdWRpZW5jZSJ9._te8D9oerdPXOKJQX0u0qnlaaQx1TMSCzAYUQBTivq8\", \"expires_in\": 604800 }"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        logd("onCreateLoginActivity")

        if(isSignedIn()){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else if(supportFragmentManager.fragments.size==0){
            supportFragmentManager.beginTransaction().replace(R.id.container,loginFragment,TAG_LOGIN_FRAGMENT).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        logd("onResumeLoginActivity")
        //TODO delete it
//        saveTokenAndFinish(mockServerResponse)
    }

    override fun onPause() {
        super.onPause()
        logd("onPauseLoginActivity")
    }

    override fun onStop() {
        super.onStop()
        logd("onStopLoginActivity")
    }

    override fun onDestroy() {
        logd("onDestroyLoginActivity")
        compositeDisposable.clear()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        logd("onSaveStateLoginActivity")

    }

    private fun isSignedIn(): Boolean{

        return !SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN,"").equals("")
    }

    override fun OnSignInButtonClicked(email: String, password: String) {

        val loginFragment: LoginFragment = supportFragmentManager.findFragmentByTag(TAG_LOGIN_FRAGMENT) as LoginFragment
        logd(loginFragment.equals(this.loginFragment).toString())
        loginFragment.resetError()
        var cancel = false
        var requestView: View? = null
        if (TextUtils.isEmpty(password)) {
            requestView = loginFragment.setPasswordError(getString(R.string.error_field_required))
            cancel = true
        }else if (!isPasswordValid(password)) {
            requestView = loginFragment.setPasswordError(getString(R.string.error_invalid_password))
            cancel = true
        }
        if (TextUtils.isEmpty(email)) {
            requestView = loginFragment.setEmailError(getString(R.string.error_field_required))
            cancel = true
        } else if (!isEmailValid(email)) {
            requestView = loginFragment.setEmailError(getString(R.string.error_invalid_email))
            cancel = true
        }
        if (!cancel)  {
            showProgress(true)
            logd("before get token")

            compositeDisposable.add(
                    RestAPI.getApi().getToken(email,password)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe({
                        result ->
                        logd(result.toString())
                        if(result.isSuccessful){
                            saveTokenAndFinish(result.body())
                        }else{
                            showProgress(false)
                            when(result.code()){
                                Constants.BAD_REQUEST -> loginFragment.setPasswordError(result.message())!!.requestFocus()
                                else -> this.requestErrorHandler(result.code(),result.message())
                            }
                        }


                    },{
                        error ->
                        showProgress(false)
                        logd(error.toString())
                        toast(error.message.toString())
                    }
                    ))
        }else{
            requestView!!.requestFocus()
        }
    }

    private fun saveTokenAndFinish(stringResult: LoginResponse?){
        SingletonSharedPref.getInstance().put(Constants.KEY_TOKEN,Constants.KEY_BEARER.plus(stringResult!!.access_token))
        showProgress(false)
        startActivity(Intent(baseContext,MainActivity::class.java))
        finish()
    }

    override fun OnSignUpTextClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.container,regFragment).addToBackStack(null).commit()
    }

    override fun OnNextButtonClicked(email: String, password: String, phone: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container,reg2Fragment).addToBackStack(null).commit()
    }

    override fun OnDoneButtonClicked(firstName: String, lastName: String) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    override fun OnUploadPhotoClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(    password: String): Boolean {
        return password.length > 4
    }

    private fun showProgress(show: Boolean) {
        if(show) loadingView!!.showLoadingIndicator()
        else loadingView!!.hideLoadingIndicator()
    }
}

