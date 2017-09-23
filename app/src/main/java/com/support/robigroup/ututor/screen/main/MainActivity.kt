package com.support.robigroup.ututor.screen.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.screen.login.LoginActivity
import com.support.robigroup.ututor.screen.topic.TeachersActivity
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.app_bar_main_nav.*

class MainActivity :
        AppCompatActivity(),
        OnMainActivityInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_nav)
        setSupportActionBar(toolbar)
        val intent = Intent()
        intent.setClass(this, NotificationService::class.java)
        startService(intent)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_payment -> {

            }
            R.id.nav_settings -> {

            }
            R.id.nav_help -> {

            }
            R.id.nav_logout -> {
                compositeDisposable.clear()
                SingletonSharedPref.getInstance().clear()
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onResume() {
        super.onResume()
        if(supportFragmentManager.fragments.size==0){
            supportFragmentManager.beginTransaction().replace(R.id.main_container, MainFragment())
                    .addToBackStack(null).commit()
        }
    }

    override fun OnClassItemClicked(item: Subject) {
        TeachersActivity.open(this,item)
    }

    override fun OnSubjectItemClicked(item: Subject) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, ClassesFragment.newInstance(item))
                .addToBackStack(null).commit()
    }

    override fun setDisplayHomeAsEnabled(showHomeAsUp: Boolean) {
        supportActionBar!!.setDisplayHomeAsUpEnabled(showHomeAsUp)
    }

    override fun setToolbarTitle(title: String) {
        supportActionBar!!.title = title
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        }else if(supportFragmentManager.backStackEntryCount==1){
            supportFragmentManager.popBackStack()
            finish()
        }else{
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
