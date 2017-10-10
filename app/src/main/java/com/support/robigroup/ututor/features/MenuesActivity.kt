package com.support.robigroup.ututor.features

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.features.account.AccountActivity
import com.support.robigroup.ututor.features.history.HistoryList
import com.support.robigroup.ututor.features.login.LoginActivity
import com.support.robigroup.ututor.features.main.MainActivity
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import kotlin.properties.Delegates

open class MenuesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var mTextMyBalance: TextView by Delegates.notNull()
    var mLanguage: TextView by Delegates.notNull()
    var mUserImage: ImageView by Delegates.notNull()
    var mFlag: ImageView by Delegates.notNull()
    var drawerLayout: DrawerLayout by Delegates.notNull()
    var navView: NavigationView by Delegates.notNull()
    var toolbar: Toolbar by Delegates.notNull()

    fun initNav(activity: Activity) {
        val view: View  = activity.findViewById(android.R.id.content)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        toolbar = view.findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        val hView = navView.getHeaderView(0)
        val nav_user = hView.findViewById<TextView>(R.id.user_name)
        nav_user.text = SingletonSharedPref.getInstance().getString(Constants.KEY_FULL_NAME)
        mTextMyBalance = hView.findViewById(R.id.my_balance)
        mLanguage = hView.findViewById(R.id.user_language)
        mFlag = hView.findViewById(R.id.flag_image)
        mUserImage = hView.findViewById(R.id.user_image)

        val toggle = ActionBarDrawerToggle(
                activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)

    }

    override fun onStart() {
        super.onStart()
        val language = Functions.getLanguage(SingletonSharedPref.getInstance().getString(Constants.KEY_LANGUAGE))
        mFlag.setImageResource(language.flagIcon)
        mLanguage.text = language.text
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_history -> {
                HistoryList.open(this)
            }
            R.id.nav_settings -> {
                AccountActivity.open(this)
            }
            R.id.nav_home_work ->{
                MainActivity.open(this)
            }
//            R.id.nav_help -> {
//
//            }
            R.id.nav_logout -> {
                SingletonSharedPref.getInstance().clear()
                stopService(Intent(this, NotificationService::class.java))
                finish()
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        }else if(supportFragmentManager.backStackEntryCount==1){
            supportFragmentManager.popBackStack()
            finish()
        }else{
            super.onBackPressed()
        }
    }
}
