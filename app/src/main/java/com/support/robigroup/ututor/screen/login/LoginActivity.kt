package com.support.robigroup.ututor.screen.login

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.model.content.User
import com.support.robigroup.ututor.screen.loading.LoadingDialog
import com.support.robigroup.ututor.screen.loading.LoadingView
import com.support.robigroup.ututor.screen.main.MainActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_login.*
import okhttp3.RequestBody
import org.json.JSONObject
import kotlin.properties.Delegates

class LoginActivity : AppCompatActivity(), OnLoginActivityInteractionListener {

    val regFragment: RegistrationFragment = RegistrationFragment()
    val reg2Fragment: RegFragment2 = RegFragment2()
    val loginFragment: LoginFragment = LoginFragment()
    var loadingView: LoadingView = LoadingDialog.view(supportFragmentManager)
    val TAG_LOGIN_FRAGMENT: String = "loginFragment"

    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var realm: Realm by Delegates.notNull()

    private val mockServerResponse: String = "{ \"access_token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJiZXliaXQ5MkBnbWFpbC5jb20iLCJqdGkiOiIwZmIwNWZjOC0zYTg5LTRhYTktYTc1Ny03NDMyNmJjZDdmMTYiLCJpYXQiOjE1MDI1MzM4NjMsIm5iZiI6MTUwMjUzMzg2MywiZXhwIjoxNTAzMTM4NjYzLCJpc3MiOiJVVHV0b3JJc3N1ZXIiLCJhdWQiOiJVVHV0b3JBdWRpZW5jZSJ9.qEKjlQdafZRSIULM39GSL006Sew9fRxpw0rsooj7kmg\", \"expires_in\": 604800 }"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        logd("onCreateLoginActivity")
        realm = Realm.getDefaultInstance()

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
        saveTokenAndFinish(mockServerResponse)
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
        realm.close() // Remember to close Realm when done.
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        logd("onSaveStateLoginActivity")

    }

    private fun isSignedIn(): Boolean{
        return realm.where(User::class.java).count()>0
    }

    override fun OnSignInButtonClicked(emailStr: String, passwordStr: String) {

        val loginFragment: LoginFragment = supportFragmentManager.findFragmentByTag(TAG_LOGIN_FRAGMENT) as LoginFragment
        logd(loginFragment.equals(this.loginFragment).toString())
        loginFragment.resetError()
        var cancel = false
        var requestView: View? = null
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
            logd("before get token")

            val data: HashMap<String,String> = HashMap()
            data.put(Constants.KEY_EMAIL,"beybit92@gmail.com")
            data.put(Constants.KEY_PASSWORD,"q1w2e3r4")
            compositeDisposable.add(
                    RestAPI.getApi().getToken(data)
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
                                else -> this.requestErrorHandler(result)
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

    private fun saveTokenAndFinish(stringResult: String?){
        val jsonResult = JSONObject(stringResult)
        realm.executeTransaction {
            realm.deleteAll()
            val user = realm.createObject(User::class.java,jsonResult.getString(Constants.KEY_RES_TOKEN))
            user.email = emailContainer.text.toString()
        }
        val user = realm.where(User::class.java).findFirst()
        logd("${user.email} ${user.firstName} ${user.token}")
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

