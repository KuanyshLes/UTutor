package com.support.robigroup.ututor.screen.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.support.design.widget.Snackbar
import android.view.*
import android.widget.Button
import android.widget.Chronometer
import com.google.gson.Gson

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.SignalRService
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.screen.main.adapters.TeachersAdapter
import com.support.robigroup.ututor.screen.main.adapters.RecentTopicsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_topic.*
import kotlinx.android.synthetic.main.topics.*
import android.os.CountDownTimer
import com.support.robigroup.ututor.model.content.*
import com.support.robigroup.ututor.screen.chat.ChatActivity
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObjectChangeListener
import io.realm.RealmResults


class TopicFragment : RxBaseFragment() {

    private var itemTopic: TopicItem = TopicItem(Id = 0)
    private var mListener: OnMainActivityInteractionListener? = null

    private var requestExists = false
    private var counter: CountDownTimer? = null

    var currentTeacher: Teacher? = null
    var currentButton: Button? = null



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

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListener!!.setDisplayHomeAsEnabled(true)
        mListener!!.setToolbarTitle(itemTopic.Text)

        topic_desc.text = itemTopic.Text
        class_text.text = "${itemTopic.classRoom} ${getString(R.string.group)}"

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
        requestSameTopics(5)
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
            requestLessonToTeacher()
            currentTeacher = item
            currentButton = itemView!!.findViewById<Button>(R.id.teacher_choose_button) as Button
        }
    }

    fun requestLessonToTeacher(teacherId: String = "bbbb", topicId: Int = 4){
        val subscription = MainManager().postLessonRequest(teacherId,topicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(activity.requestErrorHandler(teachers.code(),teachers.message())){
                                logd(Gson().toJson(teachers.body(), Lesson::class.java))
                                currentButton!!.setText(R.string.waiting)
                                currentButton!!.isClickable = false
                                requestExists = true
                                object : CountDownTimer(90000, 1000) {
                                    override fun onTick(millisUntilFinished: Long) {
                                        currentButton!!.text = getString(R.string.waiting).plus(" ").plus(millisUntilFinished / 1000)
                                    }
                                    override fun onFinish() {
                                        currentButton!!.text = getString(R.string.declined)
                                        requestExists = false
                                        removeChangeListeners()
                                    }
                                }.start()
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
        realm.executeTransaction {
            val request = realm.createObject(RequestListen::class.java,0)
            request.status = 0
        }
        val requests = realm.where(RequestListen::class.java).findAll()
        val changeListener = RealmChangeListener<RealmResults<RequestListen>> {
            rs ->
            if(rs[0].status==1){
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
        val requests = realm.where(RequestListen::class.java).findAll()
        requests.removeAllChangeListeners()
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
        counter?.cancel()
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
