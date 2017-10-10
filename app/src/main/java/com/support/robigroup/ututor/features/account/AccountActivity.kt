package com.support.robigroup.ututor.features.account

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.MenuesActivity

class AccountActivity : MenuesActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        initNav(this)
        supportActionBar?.title = getString(R.string.drawer_item_settings)

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

    companion object {
        fun open(c: Context){
            c.startActivity(Intent(c,AccountActivity::class.java))
        }
    }
}
