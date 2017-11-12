package com.support.robigroup.ututor.ui.chat

import android.net.Uri
import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmResults
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

        val info = dataManager.chatInformation
        if(!info.TeacherReady||!info.LearnerReady){
            val dif = Functions.getDifferenceInMillis(info.CreateTime)
            val utc = dif - 6*60*60*1000
            logd(utc.toString())
            if((dif>1000&&dif<Constants.WAIT_TIME)||(utc>1000&&utc<Constants.WAIT_TIME)){
                mvpView.showReadyDialog(dif)
            }else{
                mvpView.startMenuActivity()
            }
        }

        dataManager.chatMessages.addChangeListener {
            messages, changeSet ->
            if (changeSet == null) {
                notifyItemRangeInserted(0,messages.size-1)
            }else{
                val insertions = changeSet.insertionRanges
                for (range in insertions) {
                    notifyItemRangeInserted(range.startIndex, range.length)
                }
            }
        }
    }

    private fun notifyItemRangeInserted(startIndex: Int,rangeLength: Int){
        for(i in startIndex until startIndex+rangeLength){
            messagesAdapter?.addToStart(mMessages[i],true)
        }
    }

    override fun onDestroyReadyView() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartContentLoading() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(error: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCanceled() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onContentLoaded(uri: Uri?, contentType: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSelectionChanged(count: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSubmit(input: CharSequence?): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun hasContentFor(message: ChatMessage?, type: Byte): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAddAttachments() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
