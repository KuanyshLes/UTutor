package com.support.robigroup.ututor.screen.main

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.*
import android.widget.ListView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.screen.main.adapters.ListViewAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.topics.*
import kotlin.properties.Delegates

class SearchFragment : RxBaseFragment() {

    private var subject: Subject by Delegates.notNull()
    private var adapter: ListViewAdapter by Delegates.notNull()
    private var mListener: OnMainActivityInteractionListener? = null
    private var listView: ListView by Delegates.notNull()
    private var searchView: SearchView by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            subject = arguments.getParcelable(ARG_TOPICS)
        }
        setHasOptionsMenu(true)


        adapter = ListViewAdapter(activity = activity as MainActivity,
                resource = R.layout.item_search,
                searchList = subject.topics.toMutableList(),
                showEmptyResultsEnabled = true)
        listView = activity.findViewById(R.id.listview_results)
        listView.adapter = adapter

        requestTopics(subject.Id!!)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater?) {
        menu.clear()
        inflater!!.inflate(R.menu.options_menu, menu)

        val searchManager = activity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(activity.componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                logd("onQueryTextChange "+query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                logd("onQueryTextChange "+newText)
                if (TextUtils.isEmpty(newText)) {
                    adapter.filter("")
                    listView.clearTextFilter()
                } else {
                    adapter.filter(newText)
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun requestTopics(subjectId: Int) {
        val subscription = MainManager().getTopics(subjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    Functions.builtMessageWait(context)
                }.subscribe (
                        { retrievedTopics ->
                            if(activity.requestErrorHandler(retrievedTopics.code(),retrievedTopics.message())){
                                adapter.updateSearchList(retrievedTopics.body()!!.toMutableList())
                                adapter.filter("")
                                Functions.cancelProgressDialog()
                            }else{
                                //TODO handle errors
                            }
                        },
                        { e ->
                            Snackbar.make(view!!, e.message ?: "", Snackbar.LENGTH_LONG).show()
                            Functions.cancelProgressDialog()
                        }
                )
        subscriptions.add(subscription)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener!!.setDisplayHomeAsEnabled(true)
        mListener!!.setToolbarTitle("${subject.Text.toString()}, ${subject.classNumber} ${getString(R.string.class_name)}")
        adapter.filter("")


    }

    override fun onDestroyView() {
        super.onDestroyView()
        Functions.cancelProgressDialog()
        adapter.hideResults()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnMainActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {

        private val ARG_TOPICS = "param2"

        fun newInstance(subject: Subject): SearchFragment {
            val fragment = SearchFragment()
            val args = Bundle()
            args.putParcelable(ARG_TOPICS, subject)
            fragment.arguments = args
            return fragment
        }
    }
}
