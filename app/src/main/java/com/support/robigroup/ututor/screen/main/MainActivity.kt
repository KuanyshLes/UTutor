package com.support.robigroup.ututor.screen.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.screen.login.LoginActivity
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.app_bar_main_nav.*
import android.widget.TextView
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.requestErrorHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.nav_header_main_nav.*
import kotlin.properties.Delegates


class MainActivity :
        AppCompatActivity(),
        OnMainActivityInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mTextMyBalance: TextView by Delegates.notNull()

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
        val hView = nav_view.getHeaderView(0)
        val nav_user = hView.findViewById<TextView>(R.id.user_name)
        nav_user.text = SingletonSharedPref.getInstance().getString(Constants.KEY_FULL_NAME)
        mTextMyBalance = hView.findViewById(R.id.my_balance)
        requestBalance()
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
                stopService(Intent(this, NotificationService::class.java))
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

    override fun onSubjectItemClicked(item: Subject) {
        startActivity(Intent(this,ClassesActivity::class.java).putExtra(ClassesActivity.ARG_SUBJECT,item))
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

    private fun requestBalance(){
        val subscription = MainManager().getBalance()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedLessons ->
                            if(requestErrorHandler(retrievedLessons.code(),retrievedLessons.message())){
                                if(retrievedLessons.body()!=null){
                                    val myBal = retrievedLessons.body()!!.Balance
                                    SingletonSharedPref.getInstance().put(Constants.KEY_BALANCE,myBal ?: 0.0)
                                    mTextMyBalance.text = String.format("%.2f",myBal ?: 0.0)
                                }
                            }else{
                                //TODO handle http errors
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        compositeDisposable.add(subscription)
    }
}
