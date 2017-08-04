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
import com.support.robigroup.ututor.screen.main.adapters.NewsAdapter
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : RxBaseFragment() {

    companion object {
        private val KEY_REDDIT_NEWS = "redditNews"
    }

    private var redditNews: Lesson? = null
    private val newsManager by lazy { TopicsManager() }

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
        main_recycler_view_header.apply {
            setHasFixedSize(true)
        }

        initAdapter()

        if (savedInstanceState != null && savedInstanceState.containsKey(KEY_REDDIT_NEWS)) {
            redditNews = savedInstanceState.get(KEY_REDDIT_NEWS) as Lesson
            (main_recycler_view_header.adapter as NewsAdapter).clearAndAddNews(redditNews!!.news)
            logd("saved State is not null")
        } else {
            logd("saved State is null")
            requestTopics()
        }

    }

    private fun initAdapter() {
        if (main_recycler_view_header.adapter == null) {
            main_recycler_view_header.adapter = NewsAdapter()
        }
    }

    private fun requestTopics() {
        /**
         * first time will send empty string for after parameter.
         * Next time we will have redditNews set with the next page to
         * navigate with the after param.
         */
        val subscription = newsManager.getTopics(redditNews?.after ?: "")
                .subscribeOn(Schedulers.io())
                .subscribe (
                        { retrievedNews ->
                            redditNews = retrievedNews
                            (main_recycler_view_header.adapter as NewsAdapter).addNews(retrievedNews.news)
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        val news = (main_recycler_view_header.adapter as NewsAdapter).getNews()
        if (redditNews != null && news.size > 0) {
            outState.putParcelable(KEY_REDDIT_NEWS, redditNews?.copy(news = news))
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