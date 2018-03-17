package com.support.robigroup.ututor.ui.navigationDrawer.account

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.requestErrorHandler
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_change_password.*
import okhttp3.ResponseBody
import retrofit2.Response
import java.util.regex.Matcher
import java.util.regex.Pattern


class ChangePasswordActivity : AppCompatActivity() {

    private var compositeDisposable = CompositeDisposable()
    private val pattern = Pattern.compile(Constants.PASSWORD_PATTERN)
    private var matcher: Matcher? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)
        setSupportActionBar(toolbar_acc)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun onClickReady(v: View){
        hideKeyboard()
        val oldPass = old_password.text.toString()
        val newPass = old_password.text.toString()
        val confirmPass = old_password.text.toString()
        if (!validatePassword(oldPass)) {
            old_password.error = getString(R.string.error_password)
            return
        }
        if (!validatePassword(newPass)) {
            new_password.error = getString(R.string.error_password)
            return
        }
        if(!new_password.text.toString().equals(confirm_password.text.toString())){
            confirm_password.error = getString(R.string.error_not_same_password)
            return
        }
        if(new_password.text.toString().equals(old_password.text.toString())){
            old_password.error = getString(R.string.error_same_password)
            return
        }

        resetPassword(oldPass, newPass, confirmPass)
    }

    fun resetPassword(oldPass: String, confirmPass: String, newPass: String){
        val request = MainManager().resetPassword(oldPass, newPass, confirmPass).subscribe({
            response: Response<ResponseBody> ->
            if(requestErrorHandler(response.code(),response.message())){
                Snackbar.make(findViewById(android.R.id.content),getString(R.string.success_password),Snackbar.LENGTH_SHORT).show()
            }
        }, {
            e -> Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
        })
        compositeDisposable.add(request)
    }

    private fun validatePassword(password: String): Boolean {
        matcher = pattern.matcher(password)
        return matcher!!.matches()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


    companion object {
        fun open(c: Context){
            c.startActivity(Intent(c, ChangePasswordActivity::class.java))
        }
    }
}
