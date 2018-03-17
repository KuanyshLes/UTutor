package com.support.robigroup.ututor.ui.navigationDrawer.account

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.support.robigroup.ututor.R
import kotlinx.android.synthetic.main.activity_change_language.*

class ChangeLanguageActivity : AppCompatActivity() {
    var mAdapter: LanguagesAdapter = LanguagesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_language)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.language)

        list_languages.setHasFixedSize(true)
        list_languages.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        list_languages.adapter = mAdapter
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
            c.startActivity(Intent(c, ChangeLanguageActivity::class.java))
        }
    }

}
