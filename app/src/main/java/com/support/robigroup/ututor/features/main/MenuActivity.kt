package com.support.robigroup.ututor.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.features.MenuesActivity
import com.support.robigroup.ututor.features.chat.ChatActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm

class MenuActivity : MenuesActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var isChatCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initNav(this)
        supportActionBar?.title = getString(R.string.choose_type)

        val intent = Intent()
        intent.setClass(this, NotificationService::class.java)
        startService(intent)

        if(Functions.isOnline(this)){
            sendQueries()
        }else{
            Functions.builtMessageNoInternet(this,{sendQueries()})
        }


    }


    private fun sendQueries(){
        checkChatState()
        requestProfile()
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun checkChatState() {
        if(!isChatCheck)
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

    fun onClickChoose(v: View){
        when(v.id){
            R.id.choose_test -> MainActivity.open(this,2)
            R.id.choose_homework -> MainActivity.open(this,1)
        }
    }

    companion object {
        fun open(c: Context){
            c.startActivity(Intent(c, MenuActivity::class.java))
        }
    }
}
