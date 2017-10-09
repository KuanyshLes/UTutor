package com.support.robigroup.ututor.features.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.model.content.LoginResponse
import com.support.robigroup.ututor.features.loading.LoadingDialog
import com.support.robigroup.ututor.features.loading.LoadingView
import com.support.robigroup.ututor.features.main.MainActivity
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

    override fun onCreate(savedInstanceState: Bundle?) {

        //TODO move indide else if super.OnCreate method if signedIn
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        logd("onCreateLoginActivity")

        if(isSignedIn()){
            startActivity(Intent(baseContext,MainActivity::class.java))
            finish()
        }else if(supportFragmentManager.fragments.size==0){
            supportFragmentManager.beginTransaction().replace(R.id.container,loginFragment,TAG_LOGIN_FRAGMENT).commit()
        }
    }

    override fun onResume() {
        super.onResume()
        logd("onResumeLoginActivity")
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

    override fun onSignInButtonClicked(email: String, password: String) {

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
        SingletonSharedPref.getInstance().put(Constants.KEY_FULL_NAME,stringResult.FullName)
        SingletonSharedPref.getInstance().put(Constants.KEY_LANGUAGE,"kk")
        showProgress(false)
        startActivity(Intent(baseContext,MainActivity::class.java))
        finish()
    }

    override fun onSignUpTextClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.container,regFragment).addToBackStack(null).commit()
    }

    override fun onNextButtonClicked(email: String, password: String, phone: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container,reg2Fragment).addToBackStack(null).commit()
    }

    override fun onDoneButtonClicked(firstName: String, lastName: String) {
        startActivity(Intent(this,MainActivity::class.java))
        finish()
    }

    override fun onUploadPhotoClicked() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    private fun isPasswordValid(    password: String): Boolean {
        return password.length > 4
    }

    private fun showProgress(show: Boolean) {
        if(show) loadingView.showLoadingIndicator()
        else loadingView.hideLoadingIndicator()
    }
}

