package com.support.robigroup.ututor.features.feedback

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.features.MenuesActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_feedback.*


class FeedbackActivity : MenuesActivity() {

    companion object {
        fun open(c: Context){
            c.startActivity(Intent(c, FeedbackActivity::class.java))
        }
    }

    var compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        initNav(this)
        updateUI()
        supportActionBar?.title = getString(R.string.drawer_item_feedback)

    }

    fun onClickSend(v: View){
        hideKeyboard()
        postFeedback()
    }


    private fun postFeedback(){
        val text = description.text.toString()
        description.error = null
        if(text.isEmpty()){
            description.error = getString(R.string.error_field_required)
        }else{
            try {
                val pInfo = this.packageManager.getPackageInfo(packageName, 0)
                val version = pInfo.versionCode
                val subs = RestAPI.getApi().postFeedback(
                        text,
                        pInfo.versionName+version,
                        getDeviceName()!!)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe ({
                            response ->
                            if (requestErrorHandler(response.code(),response.message())) {
                                Toast.makeText(this, getString(R.string.feedback_send), Toast.LENGTH_LONG).show()
                            }
                        },{
                            error ->
                            Snackbar.make(findViewById(android.R.id.content),
                                    error.message.toString(),
                                    Snackbar.LENGTH_SHORT).show()
                        })
                compositeDisposable.add(subs)
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

        }
    }

    fun getDeviceName(): String? {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String? {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }

        return phrase.toString()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }


}
