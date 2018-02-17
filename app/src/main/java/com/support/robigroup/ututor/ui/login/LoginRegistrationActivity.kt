package com.support.robigroup.ututor.ui.login

import android.content.Intent
import android.os.Bundle
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.main.MenuActivity
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.login.login_fragment.LoginFragment
import com.support.robigroup.ututor.ui.login.reg_fragment.RegistrationFragment
import com.support.robigroup.ututor.ui.login.reg_phone_number_fragment.RegPhoneNumberFragment
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
        startActivity(Intent(baseContext, MenuActivity::class.java))
        finish()
    }

    override fun replaceLoginFragment() {
        var loginFragment = supportFragmentManager.findFragmentByTag(LoginFragment.TAG)
        if(loginFragment==null){
            loginFragment = LoginFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, loginFragment, LoginFragment.TAG)
                .addToBackStack(LoginFragment.TAG)
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
}
