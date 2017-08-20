package com.support.robigroup.ututor.screen.main

import android.content.Context
import android.os.Bundle
import android.view.*

import com.support.robigroup.ututor.R

import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import android.support.design.widget.Snackbar
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.model.content.ClassRoom
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.screen.main.adapters.ClassRoomAdapter
import com.support.robigroup.ututor.screen.main.adapters.RecentTopicsAdapter
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.topics.*


class MainFragment : RxBaseFragment() {

    companion object {
        private val KEY_RECENT_TOPICS = "recentTopics"
    }

    private var recentTopics: Subject? = null
    private var lessons: ClassRoom? = null
    private val topicsManager by lazy { MainManager() }

    private var mListener: OnMainActivityInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logd("onCreate MainFragment")
        lessons = SingletonSharedPref.getInstance().getString(SingletonSharedPref.Key.CLASS) as ClassRoom? ?: ClassRoom()

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        logd("onCreateView MainFragment")
        return inflater!!.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logd("onActivityCreated MainFragment")
        mListener!!.setToolbarTitle(getString(R.string.main_title))
        mListener!!.setDisplayHomeAsEnabled(false)
        main_recycler_view_header.apply {
            setHasFixedSize(true)
        }
        main_recycler_view_content.apply {
            setHasFixedSize(true)
        }
        initAdapters()
        requestRecentTopics(5)
    }

    override fun onResume() {
        super.onResume()
        requestSubjects(10,getString(R.string.lang))
    }

    private fun initAdapters() {
        if (main_recycler_view_header.adapter == null) {
            main_recycler_view_header.adapter = RecentTopicsAdapter()
        }
        if (main_recycler_view_content.adapter == null) {
            main_recycler_view_content.adapter = ClassRoomAdapter()
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

    private fun requestSubjects(classNumber :Int, lang: String){
        val subscription = topicsManager.getLessons(classNumber,lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedLessons ->
                            if(activity.requestErrorHandler(retrievedLessons.code(),retrievedLessons.message())){
                                lessons!!.subjects = retrievedLessons.body()!!
                                (main_recycler_view_content.adapter as ClassRoomAdapter).clearAndAddSubjects(retrievedLessons.body())
                            }else{
                                //TODO handle http errors
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
        logd("onAttach MainFragment")
        if (context is OnMainActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnMainActivityInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        logd("onDetach MainFragment")
        mListener = null
    }


}