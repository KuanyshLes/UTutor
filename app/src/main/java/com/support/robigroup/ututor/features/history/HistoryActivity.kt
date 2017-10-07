package com.support.robigroup.ututor.features.history

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.squareup.picasso.Picasso
import com.support.robigroup.ututor.Constants.BASE_URL
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.model.content.ChatHistory
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var mChatHistory: ChatHistory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        mChatHistory = intent.getParcelableExtra(ARG_CHAT_HISTORY)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.recent)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        history_class_name.text = mChatHistory.SubjectName
        history_class_text.text = String.format("%d %s", mChatHistory.Class, getString(R.string.class_name))
        teacher_name.text = mChatHistory.ChatUserName
        lesson_cost.text = mChatHistory.InvoiceSum.toString()
        lesson_duration.text = mChatHistory.Duration
        button_open_chat.setOnClickListener {
            HistoryMessages.open(this,mChatHistory)
        }
        Picasso.with(this).load(BASE_URL+mChatHistory.ChatUserProfilePhoto).into(image_photo)
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

    companion object {
        val ARG_CHAT_HISTORY = "chatHistory"
        fun open(con: Context, chatHistory: ChatHistory){
            con.startActivity(Intent(con,HistoryActivity::class.java).putExtra(ARG_CHAT_HISTORY,chatHistory))
        }
    }
}
