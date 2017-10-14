package com.support.robigroup.ututor.features.feedback

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.MenuesActivity

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

    fun onClickSend(v: View){
        Snackbar.make(v, "Отзыв отправлен!", Snackbar.LENGTH_SHORT).show()
    }


}
