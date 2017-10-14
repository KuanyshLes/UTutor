package com.support.robigroup.ututor.features.main

import android.os.Bundle
import android.view.View

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.MenuesActivity

class MenuActivity : MenuesActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        initNav(this)
        supportActionBar?.title = getString(R.string.choose_type)
        updateUI()
    }

    fun onClickChoose(v: View){
        when(v.id){
            R.id.choose_test -> MainActivity.open(this)
            R.id.choose_homework -> MainActivity.open(this)
        }
    }
}
