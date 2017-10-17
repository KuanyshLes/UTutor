package com.support.robigroup.ututor.features.feedback

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.inputmethod.InputMethodManager

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.MenuesActivity
import android.view.inputmethod.InputMethodManager.HIDE_NOT_ALWAYS
import kotlinx.android.synthetic.main.activity_feedback.*


class FeedbackActivity : MenuesActivity() {

    companion object {
        fun open(c: Context){
            c.startActivity(Intent(c, FeedbackActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)
        initNav(this)
        updateUI()
        supportActionBar?.title = getString(R.string.drawer_item_feedback)

    }

    override fun onResume() {
        super.onResume()
        leave_feedback.clearFocus()
        hideKeyboard()
    }
    fun onClickSend(v: View){
        Snackbar.make(v, "Отзыв отправлен!", Snackbar.LENGTH_SHORT).show()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val view = currentFocus
        if (view != null) {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }


}
