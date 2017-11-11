package com.support.robigroup.ututor.ui.chat

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatPresenter<V : ChatMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : BasePresenter<V>(dataManager, schedulerProvider, compositeDisposable), ChatMvpPresenter<V> {

    override fun onFinishClick() {
        mvpView.showFinishDialog()
    }

    override fun onReadyClick() {
        compositeDisposable.add(dataManager.apiHelper.postChatReady()
                .observeOn(schedulerProvider.ui())
                .subscribeOn(schedulerProvider.io())
                .subscribe({ response ->
                    if (response.isSuccessful) {
                        val res = response.body()?.charStream()?.readText()
                        if (res != null) {
                            val info = dataManager.chatInformation
                            if (res == "ready") {
                                dataManager.realm.executeTransaction {
                                    info.LearnerReady = true
                                    info.TeacherReady = true
                                }
                            } else {
                                info.LearnerReady = true
                            }
                        } else {
                            handleApiError(null)
                        }
                    } else {
                        handleApiError(ANError(response.raw()))
                    }
                }, { error ->
                    handleApiError(ANError(error))
                }))
    }

    override fun onCounterFinish() {

    }

    override fun onViewInitialized() {
        dataManager.chatInformation.addChangeListener<ChatInformation> {
            rs, changeset ->
            logd(rs.toString()+changeset.toString())
            if(changeset!=null&&!rs.isManaged&&!changeset.isDeleted){
                if(rs.StatusId == Constants.STATUS_COMPLETED){
                    mvpView.showEvalDialog()
                }else if(rs.TeacherReady&&rs.LearnerReady){
                    mvpView.closeReadyDialog()
                }else if(rs.LearnerReady&&!rs.TeacherReady){
                    mvpView.onLearnerReadyDialog()
                }
            }
        }

        mReadyDialog = ReadyDialog()
        if(!mChatInformation.TeacherReady||!mChatInformation.LearnerReady){
            mReadyDialog.isCancelable = false
            val dif = Functions.getDifferenceInMillis(mChatInformation.CreateTime)
            val utc = dif - 6*60*60*1000
            logd(utc.toString())
            if((dif>1000&&dif<Constants.WAIT_TIME)||(utc>1000&&utc<Constants.WAIT_TIME)){
                mReadyDialog.startShow(supportFragmentManager,TAG_READY_DIALOG,dif)
            }else{
                finishCorrectly()
            }
        }else if(mReadyDialog.isVisible){
            mReadyDialog.dismiss()
        }
    }
    }

    override fun onDestroyReadyView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
