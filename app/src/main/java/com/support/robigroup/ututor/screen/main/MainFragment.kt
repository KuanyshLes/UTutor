package com.support.robigroup.ututor.screen.main

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.model.content.ClassRoom
import com.support.robigroup.ututor.screen.main.adapters.ClassAdapter
import com.support.robigroup.ututor.screen.main.adapters.RecentTopicsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.topics.*


class MainFragment : RxBaseFragment() {

    companion object {
        private val KEY_RECENT_TOPICS = "recentTopics"
        private val classes: List<ClassRoom> = List(12, {index ->  ClassRoom(index)})
    }

    private var mListener: OnMainActivityInteractionListener? = null

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

//        main_recycler_view_header.apply {
//            setHasFixedSize(true)
//        }
        main_recycler_view_content.apply {
            setHasFixedSize(true)
        }
        initAdapters()
//        requestRecentTopics(5)
        mListener?.checkChatState()
    }

    private fun initAdapters() {
//        if (main_recycler_view_header.adapter == null) {
//            main_recycler_view_header.adapter = RecentTopicsAdapter()
//        }
        if (main_recycler_view_content.adapter == null) {
            main_recycler_view_content.adapter = ClassAdapter(classes,mListener)
        }
    }

    private fun requestRecentTopics(subjectId: Int) {
        val subscription = MainManager().getTopics(subjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { retrievedTopics ->
                            if(activity.requestErrorHandler(retrievedTopics.code(),retrievedTopics.message())){
                                (main_recycler_view_header.adapter as RecentTopicsAdapter).clearAndAddRecentTopics(retrievedTopics.body())
                            }else{
                                //TODO handle errors
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



}