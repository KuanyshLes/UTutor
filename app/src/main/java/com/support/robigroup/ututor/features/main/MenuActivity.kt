package com.support.robigroup.ututor.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.features.MenuesActivity
import com.support.robigroup.ututor.ui.chat.ActivityChat
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric



class MenuActivity : MenuesActivity() {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var isChatCheck = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        initNav(this)
        supportActionBar?.title = getString(R.string.choose_type)

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
                                isChatCheck = false
                            }))
    }

    private fun startTopicOrChatActivity(chatLesson: ChatLesson?){
        val realm = Realm.getDefaultInstance()
        var chats = realm.where(ChatInformation::class.java).findAll()
        var start = true
        var res: ChatInformation? = null
        if(!chats.isEmpty()){
            res = chats.last()
        }


        if(chatLesson == null || chatLesson.StatusId == Constants.STATUS_CANCELLED){
            start = false
        }else if(res == null){
            // this situation occurs when service does not work it is very rarely
            res = Functions.getChatInformation(chatLesson)
            res.deviceCreateTime = Functions.getDeviceTime()
            realm.executeTransaction {
                realm.copyToRealm(res)
            }
            start = true
        }else if(chatLesson.TeacherReady && chatLesson.LearnerReady){
            start = true
        }else{
            val dif = Functions.getDifferenceInMillis(res.deviceCreateTime!!)
            start = dif>500&&dif<Constants.WAIT_TIME
        }

        if(start){
            ActivityChat.open(this)
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
