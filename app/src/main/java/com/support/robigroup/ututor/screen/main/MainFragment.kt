package com.support.robigroup.ututor.screen.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.model.content.ChatInformation
import com.support.robigroup.ututor.model.content.ChatLesson
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.screen.main.adapters.MySubjectRecyclerViewAdapter
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.topics.*
import kotlin.properties.Delegates


class MainFragment : RxBaseFragment() {

    companion object {
        private val KEY_RECENT_TOPICS = "recentTopics"
        private val COLORS = arrayListOf("","")
    }

    private var mListener: OnMainActivityInteractionListener? = null
    private var mAdapter: MySubjectRecyclerViewAdapter by Delegates.notNull()
    private var isChatCheck = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mListener?.setToolbarTitle(getString(R.string.main_title))
        mListener?.setDisplayHomeAsEnabled(false)
        mAdapter = MySubjectRecyclerViewAdapter(ArrayList(), mListener)

//        main_recycler_view_header.apply {
//            setHasFixedSize(true)
//        }
        main_recycler_view_content.apply {
            setHasFixedSize(true)
        }
        initAdapters()
//        requestRecentTopics(5)
//        mListener?.checkChatState()
    }

    private fun initAdapters() {
//        if (main_recycler_view_header.adapter == null) {
//            main_recycler_view_header.adapter = RecentTopicsAdapter()
//        }
        if (main_recycler_view_content.adapter == null) {
            main_recycler_view_content.adapter = mAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        checkChatState()
    }

    private fun checkChatState() {
        if(!isChatCheck)
            if(Functions.isOnline(activity))
                subscriptions.add(
                        MainManager()
                                .getChatInformation()
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe({
                                    result ->
                                    isChatCheck = true
                                    if(activity.requestErrorHandler(result.code(),null)){
                                        startTopicOrChatActivity(result.body())
                                    }else{
                                        startTopicOrChatActivity(null)
                                    }
                                },{
                                    error ->
                                    logd(error.toString())
                                    activity.toast(error.message.toString())
                                    isChatCheck = false
                                }))
            else{
                Functions.builtMessageNoInternet(activity,{checkChatState()})
            }


    }

    private fun startTopicOrChatActivity(chatLesson: ChatLesson?){
        logd(SingletonSharedPref.getInstance().getString(Constants.KEY_TOKEN))
        if(chatLesson==null||chatLesson.StatusId== Constants.STATUS_COMPLETED){
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                realm.where(ChatInformation::class.java).findAll().deleteAllFromRealm()
            }
            realm.close()
            requestSubjects()
        }else{
            val realm = Realm.getDefaultInstance()
            realm.executeTransaction {
                realm.where(ChatInformation::class.java).findAll().deleteAllFromRealm()
                realm.copyToRealm(Functions.getChatInformation(chatLesson))
            }
            realm.close()
            startActivity(Intent(context, ChatActivity::class.java))
            activity.finish()
        }
    }

    private fun requestRecentTopics(subjectId: Int) {
        val subscription = MainManager().getTopics(subjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { retrievedTopics ->
                            if(activity.requestErrorHandler(retrievedTopics.code(),retrievedTopics.message())){
//                                (main_recycler_view_header.adapter as RecentTopicsAdapter).clearAndAddRecentTopics(retrievedTopics.body())
                            }
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnMainActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnMainActivityInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    private fun requestSubjects(){
        val subscription = MainManager().getLessons()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedLessons ->
                            if(activity.requestErrorHandler(retrievedLessons.code(),retrievedLessons.message())){
                                mAdapter.clearAndAddSubjects(retrievedLessons.body())
                            }else{
                                //TODO handle http errors
                            }
                        },
                        { e ->
                            Snackbar.make(view!!, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }



}