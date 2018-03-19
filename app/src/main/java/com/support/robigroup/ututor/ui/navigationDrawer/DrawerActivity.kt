package com.support.robigroup.ututor.ui.navigationDrawer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.google.gson.Gson
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.Profile
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivity
import com.support.robigroup.ututor.ui.navigationDrawer.account.AccountFragment
import com.support.robigroup.ututor.ui.navigationDrawer.feedback.FeedbackFragment
import com.support.robigroup.ututor.ui.navigationDrawer.history.HistoryChatListFragment
import com.support.robigroup.ututor.ui.navigationDrawer.main.MainFragment
import kotlinx.android.synthetic.main.activity_navigation_drawer.*
import kotlinx.android.synthetic.main.app_bar_navigation_drawer.*
import javax.inject.Inject
import android.content.ComponentName
import android.os.Build
import android.content.DialogInterface
import android.content.ActivityNotFoundException
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog


class DrawerActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener, DrawerMvpView {

    @Inject
    lateinit var mPresenter: DrawerMvpPresenter<DrawerMvpView>

    lateinit var mTextMyBalance: TextView
    lateinit var mUserName: TextView
    lateinit var mLanguage: TextView
    lateinit var mUserImage: SimpleDraweeView
    lateinit var mFlag: ImageView
    lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_drawer)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        setUp()
        mPresenter.onViewInitialized()
        showEnableNotificationDialog()
    }

    private fun showEnableNotificationDialog() {
        val xiaomi = "Xiaomi"
        val CALC_PACKAGE_NAME = "com.miui.securitycenter"
        val CALC_PACKAGE_ACITIVITY = "com.miui.permcenter.autostart.AutoStartManagementActivity"
        if (Build.BRAND.equals("xiaomi", ignoreCase = true)) {
            val builder: AlertDialog.Builder
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert)
            } else {
                builder = AlertDialog.Builder(this)
            }
            builder.setTitle("Ask for permission")
                    .setMessage("In Xiomi devices you should configure this permission by manually")
                    .setPositiveButton(android.R.string.yes, DialogInterface.OnClickListener { dialogInterface, i ->
                        try {
                            val intent = Intent()
                            intent.component = ComponentName(CALC_PACKAGE_NAME, CALC_PACKAGE_ACITIVITY)
                            this.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            Log.e("Notification Request", "Failed to launch AutoStart Screen ", e)
                        } catch (e: Exception) {
                            Log.e("Notification Request", "Failed to launch AutoStart Screen ", e)
                        }
                    })
                    .setNegativeButton(android.R.string.no, DialogInterface.OnClickListener { dialogInterface, i ->
                        dialogInterface.cancel()
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.onDetach()
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
                replaceFeedbackFragment()
            }
            R.id.nav_settings -> {
                replaceAccountFragment()
            }
            R.id.nav_home_work -> {
                replaceMainFragment()
            }
            R.id.nav_logout -> {
                mPresenter.onLogoutClicked()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun setActionBarTitle(title: String) {
        supportActionBar?.title = title
    }

    override fun stopBackgroundService() {
        Log.e("Notification Service", "Stopping servie from drawer Actiavity")
        stopService(Intent(this, NotificationService::class.java))
    }

    override fun openLoginRegistrationActivity() {
        startActivity(Intent(this, LoginRegistrationActivity::class.java))
        finish()
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
                this, drawerLayout, nav_drawer_toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        val hView = nav_view.getHeaderView(0)
        mUserName = hView.findViewById(R.id.user_name)
        mUserName.text = SingletonSharedPref.getInstance().getString(Constants.KEY_FULL_NAME)
        mTextMyBalance = hView.findViewById(R.id.my_balance)
        mLanguage = hView.findViewById(R.id.user_language)
        mFlag = hView.findViewById(R.id.flag_image)
        mUserImage = hView.findViewById(R.id.user_image)

        replaceMainFragment()
    }

    private fun replaceHistoryListFragment() {
        var registrationFragment = supportFragmentManager.findFragmentByTag(HistoryChatListFragment.TAG)
        if (registrationFragment == null) {
            registrationFragment = HistoryChatListFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.containerDrawerFragments, registrationFragment, HistoryChatListFragment.TAG)
                .addToBackStack(HistoryChatListFragment.TAG)
                .commit()
    }

    private fun replaceMainFragment() {
        var mainFragment = supportFragmentManager.findFragmentByTag(MainFragment.TAG)
        if (mainFragment == null) {
            mainFragment = MainFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.containerDrawerFragments, mainFragment, MainFragment.TAG)
                .addToBackStack(MainFragment.TAG)
                .commit()
    }

    private fun replaceAccountFragment() {
        var accountFragment = supportFragmentManager.findFragmentByTag(AccountFragment.TAG)
        if (accountFragment == null) {
            accountFragment = AccountFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.containerDrawerFragments, accountFragment, AccountFragment.TAG)
                .addToBackStack(AccountFragment.TAG)
                .commit()
    }

    private fun replaceFeedbackFragment() {
        var feedbackFragment = supportFragmentManager.findFragmentByTag(FeedbackFragment.TAG)
        if (feedbackFragment == null) {
            feedbackFragment = FeedbackFragment.newInstance()
        }
        supportFragmentManager.beginTransaction()
                .replace(R.id.containerDrawerFragments, feedbackFragment, FeedbackFragment.TAG)
                .addToBackStack(FeedbackFragment.TAG)
                .commit()
    }

    companion object {
        fun open(c: Context) {
            c.startActivity(Intent(c, DrawerActivity::class.java))
        }
    }
}
