package com.support.robigroup.ututor.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.features.account.AccountActivity
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.ChatLesson
import com.support.robigroup.ututor.commons.Subject
import com.support.robigroup.ututor.features.MenuesActivity
import com.support.robigroup.ututor.features.chat.ChatActivity
import com.support.robigroup.ututor.features.history.HistoryList
import com.support.robigroup.ututor.features.login.LoginActivity
import com.support.robigroup.ututor.features.main.adapters.SubjectsAdapter
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main_nav.*
import kotlinx.android.synthetic.main.app_bar_main_nav.*
import kotlin.properties.Delegates


class MainActivity :
        MenuesActivity(),
        OnMainActivityInteractionListener,
        NavigationView.OnNavigationItemSelectedListener {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mSubjectsAdapter: SubjectsAdapter? = null
    private var isChatCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_nav)
        initNav(this)

        val intent = Intent()
        intent.setClass(this, NotificationService::class.java)
        startService(intent)

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

    override fun onSubjectItemClicked(item: Subject) {
        ClassesActivity.open(this,item)
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
            val res = realm.where(ChatInformation::class.java).findAll()
            if(res!=null)
                realm.executeTransaction {
                    res.deleteAllFromRealm()
                }
            realm.executeTransaction {
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

    companion object {
        fun open(c: Context){
            c.startActivity(Intent(c,MainActivity::class.java))
        }
    }

}
