package com.support.robigroup.ututor.features.account

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.support.robigroup.ututor.R

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    fun onClickChanges(v: View){
        when(v.id){
            R.id.change_fio ->{

            }
            R.id.change_email ->{

            }
            R.id.change_password ->{
                ChangePasswordActivity.open(this)
            }
            R.id.change_language ->{
                ChangeLanguageActivity.open(this)
            }
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


    companion object {
        fun open(c: Context){
            c.startActivity(Intent(c,AccountActivity::class.java))
        }
    }
}
