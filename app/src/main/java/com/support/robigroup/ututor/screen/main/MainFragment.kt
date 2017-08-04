package com.support.robigroup.ututor.screen.main

import android.content.Context
import android.os.Bundle
import android.view.*

import com.support.robigroup.ututor.R

import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import android.support.design.widget.Snackbar
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.model.content.Lesson
import com.support.robigroup.ututor.screen.main.adapters.TopicsAdapter
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : RxBaseFragment() {

    companion object {
        private val KEY_REDDIT_NEWS = "recentTopics"
    }

    private var recentTopics: Lesson? = null
    private val topicsManager by lazy { TopicsManager() }

    private var mListener: OnMainActivityInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logd("onCreate MainFragment")

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
        main_recycler_view_header.setHasFixedSize(true)
        main_recycler_view_content.setHasFixedSize(true)

        initAdapter()

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_REDDIT_NEWS)) {
            recentTopics = savedInstanceState.get(KEY_REDDIT_NEWS) as Lesson
            (main_recycler_view_header.adapter as TopicsAdapter).clearAndAddNews(recentTopics!!.news)
            logd("saved State is not null")
        } else {
            logd("saved State is null")
            requestTopics()
        }

    }

    private fun initAdapter() {
        if (main_recycler_view_header.adapter == null) {
            main_recycler_view_header.adapter = TopicsAdapter()
        }
    }

    private fun requestTopics() {
        /**
         * first time will send empty string for after parameter.
         * Next time we will have recentTopics set with the next page to
         * navigate with the after param.
         */
        val subscription = topicsManager.getTopics(recentTopics?.after ?: "")
                .subscribeOn(Schedulers.io())
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


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val news = (main_recycler_view_header.adapter as TopicsAdapter).getNews()
        if (recentTopics != null && news.size > 0) {
            outState.putParcelable(KEY_REDDIT_NEWS, recentTopics?.copy(news = news))
            logd("onSaveInstanceState newsSaved")
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        menu.clear()
//        inflater.inflate(R.menu.options_menu, menu)
//        val item = menu.findItem(R.id.search)
//        val searchView = SearchView((activity as MainActivity).supportActionBar!!.themedContext)
//        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW or MenuItemCompat.SHOW_AS_ACTION_IF_ROOM)
//        MenuItemCompat.setActionView(item, searchView)
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                return false
//            }
//
//            override fun onQueryTextChange(newText: String): Boolean {
//                return false
//            }
//        })
//        logd("onCreateOptionsMenu mainFragment")
//    }

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