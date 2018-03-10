package com.support.robigroup.ututor.ui.navigationDrawer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.Gson
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.Profile
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.navigationDrawer.history.HistoryChatListFragment
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import javax.inject.Inject

class DrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerMvpView {

    @Inject
    lateinit var mPresenter: DrawerMvpPresenter<DrawerMvpView>

    lateinit var mTextMyBalance: TextView
    lateinit var mUserName: TextView
    lateinit var mLanguage: TextView
    lateinit var mUserImage: SimpleDraweeView
    lateinit var mFlag: ImageView
    lateinit var drawerLayout: DrawerLayout
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        setUp()
        mPresenter.onViewInitialized()
    }


    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else if (supportFragmentManager.backStackEntryCount == 1) {
            supportFragmentManager.popBackStack()
            finish()
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.nav_history -> {
                replaceHistoryListFragment()
            }
            R.id.nav_feedback -> {
//                FeedbackActivity.open(this)
            }
            R.id.nav_settings -> {
//                AccountActivity.open(this)
            }
            R.id.nav_home_work -> {
//                MenuActivity.open(this)
            }
            R.id.nav_logout -> {
//                SingletonSharedPref.getInstance().clear()
//                stopService(Intent(this, NotificationService::class.java))
//                finish()
//                startActivity(Intent(this, LoginRegistrationActivity::class.java))
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun updateProfile() {
        val profileString = SingletonSharedPref.getInstance().getString(Constants.KEY_PROFILE, "")
        val profile = Gson().fromJson<Profile>(profileString, Profile::class.java)
        val myBal = profile.Balance
        mTextMyBalance.text = String.format("%.0f₸", myBal ?: 0.0)
        mUserImage.setImageURI(Constants.BASE_URL + profile.ProfilePhotoPath)
        mUserName.text = profile.FullName
    }

    override fun updateLanguageAndFlag() {
        var langStr = SingletonSharedPref.getInstance().getString(Constants.KEY_LANGUAGE, "")
        if (langStr == "") {
            langStr = "kk"
            SingletonSharedPref.getInstance().put(Constants.KEY_LANGUAGE, langStr)
        }
        val language = Functions.getLanguage(langStr)
        mFlag.setImageResource(language.flagIcon)
        mLanguage.text = language.text
    }

    override fun setUp() {
        setSupportActionBar(nav_drawer_toolbar)

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, nav_drawer_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val hView = nav_view.getHeaderView(0)
        mUserName = hView.findViewById(R.id.user_name)
        mUserName.text = SingletonSharedPref.getInstance().getString(Constants.KEY_FULL_NAME)
        mTextMyBalance = hView.findViewById(R.id.my_balance)
        mLanguage = hView.findViewById(R.id.user_language)
        mFlag = hView.findViewById(R.id.flag_image)
        mUserImage = hView.findViewById(R.id.user_image)
    }

    private fun replaceHistoryListFragment() {
        var registrationFragment = supportFragmentManager.findFragmentByTag(HistoryChatListFragment.TAG)
        if(registrationFragment==null){
            registrationFragment = HistoryChatListFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.containerDrawerFragments, registrationFragment, HistoryChatListFragment.TAG)
                .addToBackStack(HistoryChatListFragment.TAG)
                .commit()
    }

    companion object {
        fun open(c: Context) {
            c.startActivity(Intent(c, DrawerActivity::class.java))
        }
    }
}
