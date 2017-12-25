package com.support.robigroup.ututor.features

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.Gson
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.Profile
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.features.account.AccountActivity
import com.support.robigroup.ututor.features.feedback.FeedbackActivity
import com.support.robigroup.ututor.ui.history.HistoryList
import com.support.robigroup.ututor.features.login.LoginActivity
import com.support.robigroup.ututor.features.main.MenuActivity
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates

open class MenuesActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    var mTextMyBalance: TextView by Delegates.notNull()
    var mUserName: TextView by Delegates.notNull()
    var mLanguage: TextView by Delegates.notNull()
    var mUserImage: SimpleDraweeView by Delegates.notNull()
    var mFlag: ImageView by Delegates.notNull()
    var drawerLayout: DrawerLayout by Delegates.notNull()
    var navView: NavigationView by Delegates.notNull()
    var toolbar: Toolbar by Delegates.notNull()
    private var subs = CompositeDisposable()

    fun initNav(activity: Activity) {
        val view: View  = activity.findViewById(android.R.id.content)
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.nav_view)
        toolbar = view.findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        val hView = navView.getHeaderView(0)
        mUserName = hView.findViewById(R.id.user_name)
        mUserName.text = SingletonSharedPref.getInstance().getString(Constants.KEY_FULL_NAME)
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

    fun updateUI(){
        val profileString = SingletonSharedPref.getInstance().getString(Constants.KEY_PROFILE,"")
        if(profileString.equals("")){
            Functions.builtMessageNoInternet(this,{requestProfile()})
        }else{
            val profile = Gson().fromJson<Profile>(profileString,Profile::class.java)
            val myBal = profile.Balance
            mTextMyBalance.text = String.format("%.0f₸",myBal ?: 0.0)
            mUserImage.setImageURI(Constants.BASE_URL+profile.ProfilePhotoPath)
            mUserName.text = profile.FullName
            Log.d("profile", profileString)
        }

    }


    fun requestProfile(){
        val subscription = MainManager().getBalance()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedLessons ->
                            if(requestErrorHandler(retrievedLessons.code(),retrievedLessons.message())){
                                if(retrievedLessons.body()!=null){
                                    val profile = retrievedLessons.body()!!
                                    val myBal = profile.Balance
                                    SingletonSharedPref.getInstance().put(Constants.KEY_BALANCE,myBal ?: 0.0)
                                    SingletonSharedPref.getInstance()
                                            .put(Constants.KEY_PROFILE, Gson().toJson(profile, Profile::class.java))
                                    updateUI()
                                }
                            }else{
                                //TODO handle http errors
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subs.add(subscription)
    }

    override fun onStart() {
        super.onStart()
        val language = Functions.getLanguage(SingletonSharedPref.getInstance().getString(Constants.KEY_LANGUAGE,"kk"))
        mFlag.setImageResource(language.flagIcon)
        mLanguage.text = language.text
    }

    override fun onDestroy() {
        super.onDestroy()
        subs.clear()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_history -> {
                HistoryList.open(this)
            }
            R.id.nav_feedback -> {
                FeedbackActivity.open(this)
            }
            R.id.nav_settings -> {
                AccountActivity.open(this)
            }
            R.id.nav_home_work ->{
                MenuActivity.open(this)
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
