package com.support.robigroup.ututor.ui.chat

import android.net.Uri
import com.androidnetworking.error.ANError
import com.stfalcon.contentmanager.ContentManager
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmResults
import retrofit2.Response
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
                mvpView.notifyItemRangeInserted(messages,0,messages.size-1)
            }else{
                val insertions = changeSet.insertionRanges
                for (range in insertions) {
                    mvpView.notifyItemRangeInserted(messages, range.startIndex, range.length)
                }
            }
        }
    }

    override fun onDestroyReadyView() {
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

    //methods to add image from gallery or other resources
    override fun onStartContentLoading() {
        TODO("not implemented")
    }

    override fun onError(error: String?) {
        handleApiError(ANError(error))
    }

    override fun onCanceled() {

    }

    override fun onContentLoaded(uri: Uri?, contentType: String?) {
        if (contentType.equals(ContentManager.Content.IMAGE.toString())) {
            if(uri!=null){
                sendFileMessage(uri.path)
            }
        }
    }

    private fun sendFileMessage(imageUri: String){
        val encodedImage = Functions.getEncodedImage(imageUri)
        if(encodedImage!=null){
            compositeDisposable.add(sendMessage(file64base = encodedImage)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe(
                            { messageResponse ->
                                if(messageResponse.isSuccessful){
                                    val message: ChatMessage? = messageResponse.body()
                                    if(message!=null){
                                        dataManager.realm.executeTransaction {
                                            dataManager.realm.copyToRealmOrUpdate(message)
                                        }
                                    }else{
                                        handleApiError(null)
                                    }
                                }else{
                                    handleApiError(ANError(messageResponse.raw()))
                                }
                            },
                            { e ->
                                handleApiError(ANError(e))
                            }
                    )
            )
        }
    }

    private fun sendMessage(messageText: String? = null, file64base: String? = null): Flowable<Response<ChatMessage>> =
            if(file64base != null&&messageText!=null){
                val res: HashMap<String,String> = HashMap()
                res.put("File",file64base)
                res.put("Message",messageText)
                dataManager.apiHelper.postMessagePhoto(res)
            } else if(file64base!=null) {
                val res: HashMap<String,String> = HashMap()
                res.put("File",file64base)
                dataManager.apiHelper.postMessagePhoto(res)
            }else if(messageText!=null) dataManager.apiHelper.postTextMessage(messageText)
            else Flowable.empty()

}
