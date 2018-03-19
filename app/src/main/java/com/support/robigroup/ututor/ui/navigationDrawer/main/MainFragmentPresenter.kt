package com.support.robigroup.ututor.ui.navigationDrawer.main

import android.util.Log
import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.ChatLesson
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import java.net.HttpURLConnection
import javax.inject.Inject


class MainFragmentPresenter<V : MainFragmentMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable), MainFragmentMvpPresenter<V> {

    override fun onViewInitialized() {
        checkChatState()
    }

    private fun checkChatState() {
        compositeDisposable.add(
                dataManager.apiHelper
                        .getInformationAboutChat()
                        .observeOn(schedulerProvider.ui())
                        .subscribeOn(schedulerProvider.io())
                        .subscribe({ result ->
                            if(result.isSuccessful){
                                checkChatAndOpenIfExists(result.body())
                            }else if(result.code() == HttpURLConnection.HTTP_BAD_REQUEST){
                                clearChatData()
                            }else{
                                handleApiError(ANError(result.raw()))
                            }
                        }, { error ->
                            handleApiError(ANError(error))
                        }
                        ))

    }

    private fun checkChatAndOpenIfExists(chatLesson: ChatLesson?){

        if(isChatValidNow(chatLesson)){
            if(chatInformation == null){
                createChatInLocalDatabaseFromServer(chatLesson!!)
                Log.e("chat","c1")
            }
            if((chatLesson!!.TeacherReady && chatLesson.LearnerReady)
                    || isChatWaitTimeExpired(chatInformation)){
                mvpView.openChat()
                Log.e("chat","c2")
            }else {
                clearChatData()
                Log.e("chat","c3")
            }
        }else{
            clearChatData()
            Log.e("chat","c4")
        }
    }

    private fun isChatValidNow(lesson: ChatLesson?): Boolean
            = !(lesson == null || lesson.StatusId == Constants.STATUS_CANCELLED)

    private fun isChatWaitTimeExpired(localChatLesson: ChatInformation): Boolean{
        val dif = Functions.getDifferenceInMillis(localChatLesson.deviceCreateTime!!)
        return dif>500&&dif<Constants.WAIT_TIME
    }

    private fun createChatInLocalDatabaseFromServer(chatLesson: ChatLesson){
        val res = Functions.getChatInformation(chatLesson)
        res.deviceCreateTime = Functions.getDeviceTime()
        realm.executeTransaction {
            realm.copyToRealm(res)
        }
        Log.e("chat","databaseChat")
    }

    private fun clearChatData(){
        Log.e("chat","clearChatData")
        realm.executeTransaction {
            chatMessages.deleteAllFromRealm()
            chatInformation?.deleteFromRealm()
        }
    }

}