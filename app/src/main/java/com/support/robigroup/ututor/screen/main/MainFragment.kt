package com.support.robigroup.ututor.screen.main

import android.content.Context
import android.os.Bundle
import android.view.*

import com.support.robigroup.ututor.R

import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import android.support.design.widget.Snackbar
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.model.content.ClassRoom
import com.support.robigroup.ututor.model.content.Lesson
import com.support.robigroup.ututor.screen.main.adapters.ClassRoomAdapter
import com.support.robigroup.ututor.screen.main.adapters.TopicsAdapter
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.topics.*


class MainFragment : RxBaseFragment() {

    companion object {
        private val KEY_RECENT_TOPICS = "recentTopics"
    }

    private var recentTopics: Lesson? = null
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

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_RECENT_TOPICS)) {
            recentTopics = savedInstanceState.get(KEY_RECENT_TOPICS) as Lesson
            (main_recycler_view_header.adapter as TopicsAdapter).clearAndAddNews(recentTopics!!.news)
            logd("saved State is not null")
        } else {
            logd("saved State is null")
        }
    }

    override fun onResume() {
        super.onResume()
        requestTopics()
        requestLessons()
    }

    private fun initAdapters() {
        if (main_recycler_view_header.adapter == null) {
            main_recycler_view_header.adapter = TopicsAdapter()
        }
        if (main_recycler_view_content.adapter == null) {
            main_recycler_view_content.adapter = ClassRoomAdapter()
        }
    }

    private fun requestTopics() {
        val subscription = topicsManager.getTopics(recentTopics?.after ?: "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { retrievedTopics ->
                            recentTopics = retrievedTopics
                            (main_recycler_view_header.adapter as TopicsAdapter).addNews(retrievedTopics.news)
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }

    private fun requestLessons(){
        val subscription = topicsManager.getLessons()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedLessons ->
                            lessons = retrievedLessons
                            logd("size2 ".plus(retrievedLessons.lessons.size))
                            (main_recycler_view_content.adapter as ClassRoomAdapter).clearAndAddNews(retrievedLessons.lessons)
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "bbbb", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        logd("onSaveInstanceState main fragment newsSaved")
//        val news = (main_recycler_view_header.adapter as TopicsAdapter).getNews()
//        if (recentTopics != null && news.isNotEmpty()) {
//            outState.putParcelable(KEY_RECENT_TOPICS, recentTopics?.copy(news = news))
//
//        }
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