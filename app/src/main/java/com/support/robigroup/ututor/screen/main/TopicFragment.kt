package com.support.robigroup.ututor.screen.main

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.*

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.ClassRoom
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.main.adapters.TeachersAdapter
import com.support.robigroup.ututor.screen.main.adapters.TopicsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_topic.*
import kotlinx.android.synthetic.main.topics.*


class TopicFragment : RxBaseFragment() {

    private var itemTopic: TopicItem = TopicItem("Error","Error","Error")

    private var lessons: ClassRoom? = null


    private var mListener: OnMainActivityInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            itemTopic = arguments.getParcelable(ARG_TOPIC_ITEM)
        }
    }



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_topic, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mListener!!.setDisplayHomeAsEnabled(true)
        mListener!!.setToolbarTitle(itemTopic.lesson)

        topic_desc.text = itemTopic.description
        class_text.text = "${itemTopic.group} ${getString(R.string.group)}"

        find_teacher.setOnClickListener {
            requestTeacher()
        }

        main_recycler_view_header.apply {
            setHasFixedSize(true)
        }
        initAdapters()
    }

    override fun onResume() {
        super.onResume()
        requestTopics()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu!!.clear()
    }

    fun initAdapters(){
        if (main_recycler_view_header.adapter == null) {
            main_recycler_view_header.adapter = TopicsAdapter()
        }
        if(list_teachers.adapter == null){
            list_teachers.adapter = TeachersAdapter()
        }
    }

    private fun requestTopics() {
        val subscription = MainManager().getTopics("")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { retrievedTopics ->
                            (main_recycler_view_header.adapter as TopicsAdapter).addNews(retrievedTopics.news)
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }

    private fun requestTeacher() {
        val subscription = MainManager().getTeachers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            logd(teachers.teachers.size.toString())

                            (list_teachers.adapter as TeachersAdapter).clearAndAddNews(teachers.teachers)
                            hideFindButton(teachers.teachers.size)
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }

    private fun hideFindButton(size: Int){
        number_of_teachers.text = getString(R.string.teachers_accepted).plus(": ").plus(size)
        number_of_teachers.visibility = View.VISIBLE
        find_teacher.visibility = View.GONE
        topics_bottom.visibility = View.GONE
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

        private val ARG_TOPIC_ITEM = "topicItem"

        fun newInstance(item: TopicItem): TopicFragment {
            val fragment = TopicFragment()
            val args = Bundle()
            args.putParcelable(ARG_TOPIC_ITEM, item)
            fragment.arguments = args
            return fragment
        }
    }
}
