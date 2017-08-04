package com.support.robigroup.ututor.screen.login

import android.annotation.SuppressLint
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnLoginActivityInteractionListener
import com.support.robigroup.ututor.commons.toast
import com.support.robigroup.ututor.model.content.User
import com.support.robigroup.ututor.screen.loading.LoadingDialog
import com.support.robigroup.ututor.screen.loading.LoadingView
import com.support.robigroup.ututor.screen.main.MainActivity
import io.realm.Realm
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity(), OnLoginActivityInteractionListener {

    val regFragment: RegistrationFragment = RegistrationFragment()
    val reg2Fragment: RegFragment2 = RegFragment2()
    private var mAuthTask: UserLoginTask? = null
    var loadingView: LoadingView? = null
    val TAG_LOGIN_FRAGMENT: String = "loginFragment"

    private var realm: Realm by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        realm = Realm.getDefaultInstance()
        loadingView = LoadingDialog.view(supportFragmentManager)
    }

    override fun onResume() {
        super.onResume()

        if(isSignedIn()){
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }else if(supportFragmentManager.fragments.size==0){
            supportFragmentManager.beginTransaction().replace(R.id.container,LoginFragment(),TAG_LOGIN_FRAGMENT).commit()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }

    private fun isSignedIn(): Boolean{
        return realm.where(User::class.java).count()>0
    }

    override fun OnSignInButtonClicked(emailStr: String, passwordStr: String) {
        if (mAuthTask != null) {
            return
        }
        val loginFragment: LoginFragment = supportFragmentManager.findFragmentByTag(TAG_LOGIN_FRAGMENT) as LoginFragment
        loginFragment.resetError()


        var cancel = false
        var requestView: View? = null

        // Check for a valid password, if the user entered one.

        if (TextUtils.isEmpty(passwordStr)) {
            requestView = loginFragment.setPasswordError(getString(R.string.error_field_required))
            cancel = true
        }else if (!isPasswordValid(passwordStr)) {
            requestView = loginFragment.setPasswordError(getString(R.string.error_invalid_password))
            cancel = true
        }

        if (TextUtils.isEmpty(emailStr)) {
            requestView = loginFragment.setEmailError(getString(R.string.error_field_required))
            cancel = true
        } else if (!isEmailValid(emailStr)) {
            requestView = loginFragment.setEmailError(getString(R.string.error_invalid_email))
            cancel = true
        }

        if (!cancel)  {
            showProgress(true)
            mAuthTask = UserLoginTask(emailStr, passwordStr)
            mAuthTask!!.execute(null as Void?)
        }else{
            requestView!!.requestFocus()
        }
    }

    override fun OnSignUpTextClicked() {
        supportFragmentManager.beginTransaction().replace(R.id.container,regFragment).addToBackStack(null).commit()
    }

    override fun OnNextButtonClicked(email: String, password: String, phone: String) {
        supportFragmentManager.beginTransaction().replace(R.id.container,reg2Fragment).addToBackStack(null).commit()
    }

    override fun OnDoneButtonClicked(firstName: String, lastName: String) {
        startActivity(Intent(this,MainActivity::class.java))
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

    @SuppressLint("StaticFieldLeak")
    inner class UserLoginTask internal constructor(private val mEmail: String, private val mPassword: String) : AsyncTask<Void, Void, Boolean>() {

        override fun doInBackground(vararg params: Void): Boolean? {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(5000)
            } catch (e: InterruptedException) {
                return false
            }

            return DUMMY_CREDENTIALS
                    .map { it.split(":") }
                    .firstOrNull { it[0] == mEmail }
                    ?.let {
                        it[1] == mPassword
                    }
                    ?: false
        }

        override fun onPostExecute(success: Boolean?) {
            mAuthTask = null

            if (success!!) {
                finish()
            } else {
                showProgress(false)
                val loginFragment: LoginFragment? = supportFragmentManager.findFragmentByTag(TAG_LOGIN_FRAGMENT) as LoginFragment
                if(loginFragment!=null){
                    toast("Fragment is not empty")
                    loginFragment.setPasswordError(getString(R.string.error_incorrect_password))!!.requestFocus()
                }else{
                    toast("Fragment is empty")

                }

            }
        }

        override fun onCancelled() {
            mAuthTask = null
            showProgress(false)
        }
    }


    companion object {
        private val DUMMY_CREDENTIALS = arrayOf("foo@example.com:hello", "bar@example.com:world")
    }
}
