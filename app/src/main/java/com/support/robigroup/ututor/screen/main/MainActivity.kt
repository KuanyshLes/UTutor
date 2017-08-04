package com.support.robigroup.ututor.screen.main

import android.app.SearchManager
import android.content.Context
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.widget.SearchView

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.logd
import android.view.MenuInflater



class MainActivity : AppCompatActivity(), OnMainActivityInteractionListener {
    override fun onFragmentInteraction(uri: Uri) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        private val TAG_MAIN_FRAGMENT: String = "mainFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logd("onCreate MainActivity")

    }

    override fun onResume() {
        super.onResume()
        logd("onResume MainActivity")
        if(supportFragmentManager.fragments.size==0){
            supportFragmentManager.beginTransaction().replace(R.id.main_container, MainFragment(),TAG_MAIN_FRAGMENT).commit()
        }
    }
}
