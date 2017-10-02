package com.support.robigroup.ututor.screen.main

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.TextView
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.model.content.ChatInformation
import com.support.robigroup.ututor.model.content.ChatLesson
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.screen.history.HistoryList
import com.support.robigroup.ututor.screen.login.LoginActivity
import com.support.robigroup.ututor.screen.main.adapters.SubjectsAdapter
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.app_bar_main_nav.*
import kotlin.properties.Delegates


class MainActivity :
        AppCompatActivity(),
        OnMainActivityInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mTextMyBalance: TextView by Delegates.notNull()
    private var mSubjectsAdapter: SubjectsAdapter? = null
    private var isChatCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_nav)
        setSupportActionBar(toolbar)
        val intent = Intent()
        intent.setClass(this, NotificationService::class.java)
        startService(intent)

        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.title = title

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        val hView = nav_view.getHeaderView(0)
        val nav_user = hView.findViewById<TextView>(R.id.user_name)
        nav_user.text = SingletonSharedPref.getInstance().getString(Constants.KEY_FULL_NAME)
        mTextMyBalance = hView.findViewById(R.id.my_balance)

        initAdapters()
        swipe_container.setOnRefreshListener {
            requestSubjects()
        }
        sendQueries()
    }

    private fun initAdapters() {
        mSubjectsAdapter = SubjectsAdapter(ArrayList(), this)
        list_subjects.apply {
            setHasFixedSize(true)
            if(adapter == null){
                adapter = mSubjectsAdapter
            }
        }
    }

    private fun sendQueries(){
        checkChatState()
        requestBalance()
        requestSubjects()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_history -> {
                HistoryList.open(this)
            }
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

    override fun onSubjectItemClicked(item: Subject) {
        ClassesActivity.open(this,item)
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

    private fun checkChatState() {
        if(!isChatCheck)
            if(Functions.isOnline(this))
                compositeDisposable.add(
                        MainManager()
                                .getChatInformation()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                    result ->
                                    isChatCheck = true
                                    if(requestErrorHandler(result.code(),null)){
                                        startTopicOrChatActivity(result.body())
                                    }else{
                                        startTopicOrChatActivity(null)
                                    }
                                },{
                                    error ->
                                    logd(error.toString())
                                    toast(error.message.toString())
                                    isChatCheck = false
                                }))
            else{
                Functions.builtMessageNoInternet(this,{checkChatState()})
            }


    }

    private fun startTopicOrChatActivity(chatLesson: ChatLesson?){
        if(chatLesson==null||chatLesson.StatusId== Constants.STATUS_COMPLETED){
            val realm = Realm.getDefaultInstance()
            val res = realm.where(ChatInformation::class.java).findAll()
            if(res!=null)
            realm.executeTransaction {
                res.deleteAllFromRealm()
            }
            realm.close()
        }else{
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                realm.where(ChatInformation::class.java).findAll().deleteAllFromRealm()
                realm.copyToRealm(Functions.getChatInformation(chatLesson))
            }
            realm.close()
            ChatActivity.open(this)
            finish()
        }
    }

    private fun requestSubjects(){
        val subscription = MainManager().getSubjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedLessons ->
                            if(requestErrorHandler(retrievedLessons.code(),retrievedLessons.message())){
                                mSubjectsAdapter?.updateSubjects(retrievedLessons.body())
                            }
                            if(swipe_container.isRefreshing)
                                swipe_container.isRefreshing = false
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        compositeDisposable.add(subscription)
    }

}
