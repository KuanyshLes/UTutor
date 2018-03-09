package com.support.robigroup.ututor.ui.login

import android.os.Bundle
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.main.MenuActivity
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.login.loginFragment.LoginFragment
import com.support.robigroup.ututor.ui.login.regFragment.RegistrationFragment
import com.support.robigroup.ututor.ui.login.regPhoneNumberFragment.RegPhoneNumberFragment
import com.support.robigroup.ututor.ui.login.setPasswordFragment.SetPasswordFragment
import com.support.robigroup.ututor.ui.login.verifyPhoneNumberFragment.VerifyPhoneNumberFragment
import javax.inject.Inject

class LoginRegistrationActivity : BaseActivity(), LoginRegistrationActivityMvpView {

    @Inject
    lateinit var mPresenter: LoginRegistrationActivityMvpPresenter<LoginRegistrationActivityMvpView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        mPresenter.onViewInitialized()
    }


    override fun setUp() {

    }

    override fun startMenuActivity() {
        if(SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN, "").length > 10){
            MenuActivity.open(this)
            finish()
        }
    }

    override fun replaceLoginFragment() {
        var loginFragment = supportFragmentManager.findFragmentByTag(LoginFragment.TAG)
        if(loginFragment==null){
            loginFragment = LoginFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, loginFragment, LoginFragment.TAG)
                .commit()
    }

    override fun replaceRegistrationFragment() {
        var registrationFragment = supportFragmentManager.findFragmentByTag(RegistrationFragment.TAG)
        if(registrationFragment==null){
            registrationFragment = RegistrationFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, registrationFragment, RegistrationFragment.TAG)
                .addToBackStack(RegistrationFragment.TAG)
                .commit()
    }

    override fun replaceRegPhoneNumberFragment() {
        var regPhoneNumberFragment = supportFragmentManager.findFragmentByTag(RegPhoneNumberFragment.TAG)
        if(regPhoneNumberFragment==null){
            regPhoneNumberFragment = RegPhoneNumberFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, regPhoneNumberFragment, RegPhoneNumberFragment.TAG)
                .addToBackStack(RegPhoneNumberFragment.TAG)
                .commit()
    }

    override fun replaceVerifyPhoneNumberFragment() {
        var verifyPhoneNumberFragment = supportFragmentManager.findFragmentByTag(VerifyPhoneNumberFragment.TAG)
        if(verifyPhoneNumberFragment==null){
            verifyPhoneNumberFragment = VerifyPhoneNumberFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, verifyPhoneNumberFragment, RegPhoneNumberFragment.TAG)
                .addToBackStack(VerifyPhoneNumberFragment.TAG)
                .commit()
    }

    override fun replaceSetPasswordFragment() {
        var setPasswordFragment = supportFragmentManager.findFragmentByTag(SetPasswordFragment.TAG)
        if(setPasswordFragment==null){
            setPasswordFragment = SetPasswordFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, setPasswordFragment, SetPasswordFragment.TAG)
                .addToBackStack(SetPasswordFragment.TAG)
                .commit()
    }
}
