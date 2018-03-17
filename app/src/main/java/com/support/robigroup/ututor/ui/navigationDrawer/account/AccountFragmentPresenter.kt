package com.support.robigroup.ututor.ui.navigationDrawer.account

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

/**
 * Created by Bimurat Mukhtar on 10.03.2018.
 */
class AccountFragmentPresenter<V : AccountFragmentMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable), AccountFragmentMvpPresenter<V> {

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
            }
            if((chatLesson!!.TeacherReady && chatLesson.LearnerReady)
                    || (!isChatWaitTimeExpired(chatInformation))){
                mvpView.openChat()
            }else {
                clearChatData()
            }
        }else{
            clearChatData()
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
    }

    private fun clearChatData(){
        chatMessages.deleteAllFromRealm()
        chatInformation?.deleteFromRealm()
    }

}