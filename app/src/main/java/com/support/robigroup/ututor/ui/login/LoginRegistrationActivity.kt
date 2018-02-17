package com.support.robigroup.ututor.ui.login

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.main.MenuActivity
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.login.login_fragment.LoginFragment
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

    override fun openMainActivity() {
        startActivity(Intent(baseContext, MenuActivity::class.java))
        finish()
    }

    override fun replaceLoginFragment() {
        var loginFragment = supportFragmentManager.findFragmentByTag(LoginFragment.TAG)
        if(loginFragment==null){
            loginFragment = LoginFragment.newInstance()
        }
        supportFragmentManager.beginTransaction().replace(R.id.container, loginFragment, LoginFragment.TAG).commit()
    }

    override fun replaceRegistrationFragment() {

    }
}
