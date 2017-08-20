package com.support.robigroup.ututor.screen.topic

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.*
import android.widget.Button
import com.google.gson.Gson
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.model.content.LessonRequestForTeacher
import com.support.robigroup.ututor.model.content.RequestListen
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.screen.main.adapters.RecentTopicsAdapter
import com.support.robigroup.ututor.screen.topic.adapters.TeachersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlinx.android.synthetic.main.fragment_topic.*
import kotlinx.android.synthetic.main.topics.*
import kotlin.properties.Delegates


class TopicFragment : RxBaseFragment() {

    private var itemTopic: TopicItem by Delegates.notNull()
    private var mListener: OnMainActivityInteractionListener? = null

    private var requestExists = false
    var currentTeacher: Teacher? = null
    var currentButton: Button? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if(savedInstanceState!=null){
            itemTopic = savedInstanceState.getParcelable(ARG_TOPIC_ITEM)
        }else{
            itemTopic = arguments.getParcelable(ARG_TOPIC_ITEM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_topic, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListener!!.setDisplayHomeAsEnabled(true)
        mListener!!.setToolbarTitle(itemTopic.Text ?: "error")

        topic_desc.text = itemTopic.Text
        class_text.text = "${itemTopic.classRoom} ${getString(R.string.group)}"

        find_teacher.setOnClickListener {
            requestTeacher()
        }

        main_recycler_view_header.apply {
            setHasFixedSize(true)
        }
        initAdapters()
        requestSameTopics(5)



    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(ARG_TOPIC_ITEM, itemTopic)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu!!.clear()
    }

    fun initAdapters(){
        if (main_recycler_view_header.adapter == null) {
            main_recycler_view_header.adapter = RecentTopicsAdapter()
        }
        if(list_teachers.adapter == null){
            list_teachers.adapter = TeachersAdapter(this)
        }
    }

    private fun requestSameTopics(subjectId: Int) {
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

    private fun requestTeacher() {
        val subscription = MainManager().getTeachers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(activity.requestErrorHandler(teachers.code(),teachers.message())){
                                (list_teachers.adapter as TeachersAdapter).clearAndAddNews(teachers.body()!!)
                                hideFindButton(teachers.body()!!.size)
                            }else{
                                //TODO handle server errors
                            }
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                )
        subscriptions.add(subscription)
    }

    fun onTeacherItemClicked(item: Teacher,itemView: View? ){
        if(!requestExists){
            requestLessonToTeacher(item.Id,itemTopic.Id ?: 4)
            currentTeacher = item
            currentButton = itemView!!.findViewById<Button>(R.id.teacher_choose_button) as Button
        }
    }

    fun requestLessonToTeacher(teacherId: String , topicId: Int ){

        val subscription = MainManager().postLessonRequest(teacherId,topicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(activity.requestErrorHandler(teachers.code(),teachers.message())){
                                logd(Gson().toJson(teachers.body(), LessonRequestForTeacher::class.java))
                                currentButton!!.setText(R.string.waiting)
                                requestExists = true
                                setRealmOnChangeListener()
                            }else{
                                //TODO handle server errors
                            }
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                )
        subscriptions.add(subscription)
    }

    fun setRealmOnChangeListener(){
        val realm: Realm = Realm.getDefaultInstance()
        var request = realm.where(RequestListen::class.java).findFirst()
        realm.executeTransaction {
            if(request==null){
                request = realm.createObject(RequestListen::class.java,0)
            }
            request.status = 0
        }
        val requests = realm.where(RequestListen::class.java).findFirst()
        val changeListener = RealmChangeListener<RequestListen> {
            rs ->
            if(rs.status==1){
                ChatActivity.open(this.activity,currentTeacher)
            }else{
                requestExists = false
                currentButton!!.setText(R.string.declined)
            }
        }
        requests.addChangeListener(changeListener)


    }

    fun removeChangeListeners(){
        val realm: Realm = Realm.getDefaultInstance()
        val request: RequestListen? = realm.where(RequestListen::class.java).findFirst()
        request?.removeAllChangeListeners()
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

    override fun onStop() {
        super.onStop()
        removeChangeListeners()

    }

    companion object {
        val ARG_TOPIC_ITEM = "topicItem"
        fun newInstance(item: TopicItem): TopicFragment {
            val fragment = TopicFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_TOPIC_ITEM,item)
            fragment.arguments = bundle
            return fragment
        }
    }
}
