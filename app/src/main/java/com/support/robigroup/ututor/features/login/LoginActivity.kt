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
import com.support.robigroup.ututor.commons.LoginResponse
import com.support.robigroup.ututor.features.loading.LoadingDialog
import com.support.robigroup.ututor.features.loading.LoadingView
import com.support.robigroup.ututor.features.main.MenuActivity
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class LoginActivity : AppCompatActivity(), OnLoginActivityInteractionListener {

    private val regFragment: RegistrationFragment = RegistrationFragment()
    private val getCodeFragment: GetCodeFragment = GetCodeFragment()
    private val setPasswordFragment: SetPasswordFragment = SetPasswordFragment()
    private val verifyCodeFragment: VerifyCodeFragment = VerifyCodeFragment()
    private val loginFragment: LoginFragment = LoginFragment()
    var loadingView: LoadingView = LoadingDialog.view(supportFragmentManager)
    private val TAG_LOGIN_FRAGMENT: String = "loginFragment"
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var firstToken: String? = null
    private var phoneNumber: String? = null
    private var secondToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        //TODO move indide else if super.OnCreate method if signedIn
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        if(isSignedIn()){
            startActivity(Intent(baseContext, MenuActivity::class.java))
            finish()
        }else if(supportFragmentManager.fragments.size==0){
            supportFragmentManager.beginTransaction().replace(R.id.container,loginFragment,TAG_LOGIN_FRAGMENT).commit()
        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
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
        startActivity(Intent(baseContext, MenuActivity::class.java))
        finish()
    }

    override fun onSignUpTextClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.container,regFragment).addToBackStack(null).commit()
    }

    override fun onNextButtonClicked(token: String) {
        firstToken = token
        supportFragmentManager.beginTransaction().replace(R.id.container, getCodeFragment).addToBackStack(null).commit()
    }

    override fun onVerifyCodeButtonClicked(token: String) {
        secondToken = token
        supportFragmentManager.beginTransaction().replace(R.id.container, setPasswordFragment).addToBackStack(null).commit()
    }

    override fun onGetCodeButtonClicked(phone: String) {
        this.phoneNumber = phone
        supportFragmentManager.beginTransaction().replace(R.id.container, verifyCodeFragment).addToBackStack(null).commit()
    }

    override fun onSetPasswordButtonClicked(token: String) {
        logd("token is "+ token)
        SingletonSharedPref.getInstance().put(Constants.KEY_TOKEN, token)
        SingletonSharedPref.getInstance().put(Constants.KEY_LANGUAGE,"kk")
        MenuActivity.open(this)
    }

    override fun getFirstToken(): String{
        return firstToken!!
    }
    override fun getSecondToken(): String {
        return secondToken!!
    }
    override fun getPhoneNumber(): String {
        return phoneNumber!!
    }

    override fun onUploadPhotoClicked() {
        TODO("not implemented") //To change body of created functions use FilePath | Settings | FilePath Templates.
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

