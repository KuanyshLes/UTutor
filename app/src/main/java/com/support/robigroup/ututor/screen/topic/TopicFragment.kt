package com.support.robigroup.ututor.screen.topic

import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.design.widget.Snackbar
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.google.gson.Gson
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.model.content.LessonRequestForTeacher
import com.support.robigroup.ututor.model.content.RequestListen
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.screen.main.adapters.RecentTopicsAdapter
import com.support.robigroup.ututor.screen.topic.adapters.TeachersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlinx.android.synthetic.main.fragment_topic.*
import kotlinx.android.synthetic.main.topics.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class TopicFragment : RxBaseFragment() {

    private var itemTopic: TopicItem by Delegates.notNull()
    private var mListener: OnMainActivityInteractionListener? = null

    var currentTeacher: Teacher? = null
    var currentButton: Button? = null
    var currentStatus: Int = Constants.STATUS_NOT_REQUESTED
    var countDownCounter: CountDownTimer? = null
    private var changeListener: RealmChangeListener<RequestListen>? = null
    private var realm: Realm by Delegates.notNull()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        if(savedInstanceState!=null){
            itemTopic = savedInstanceState.getParcelable(ARG_TOPIC_ITEM)
        }else{
            itemTopic = arguments.getParcelable(ARG_TOPIC_ITEM)
        }

        setHasOptionsMenu(true)
        realm = Realm.getDefaultInstance()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_topic, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mListener!!.setDisplayHomeAsEnabled(true)
        mListener!!.setToolbarTitle(itemTopic.subject?.Text ?: "error")

        topic_desc.text = itemTopic.Text
        class_text.text = "${itemTopic.subject?.classNumber} ${getString(R.string.group)}"

        find_teacher.setOnClickListener {
            requestTeacher()
        }

//        main_recycler_view_header.apply {
//            setHasFixedSize(true)
//        }
        initAdapters()
//        requestSameTopics(5)



    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(ARG_TOPIC_ITEM, itemTopic)
        if(currentStatus>Constants.STATUS_NOT_REQUESTED){
            outState?.putParcelable(ARG_TEACHER,currentTeacher)
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu!!.clear()
        super.onPrepareOptionsMenu(menu)
    }

    fun initAdapters(){
//        if (main_recycler_view_header.adapter == null) {
//            main_recycler_view_header.adapter = RecentTopicsAdapter()
//        }
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
                                logd(Gson().toJson(teachers.body()!![0],Teacher::class.java))
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
        when(currentStatus){
            Constants.STATUS_NOT_REQUESTED ->{
                requestLessonToTeacher(item.Id,itemTopic.Id ?: 4)
                currentStatus = Constants.STATUS_REQUESTED
                currentTeacher = item
                currentButton = itemView!!.findViewById<Button>(R.id.teacher_choose_button) as Button
            }
            Constants.STATUS_REQUESTED ->{
                Snackbar.make(main_recycler_view_header, getString(R.string.error_request_exists), Snackbar.LENGTH_LONG).show()
            }
            Constants.STATUS_ACCEPTED->{
                postLearnerReady()
            }
            Constants.STATUS_TEACHER_CONFIRMED->{
                postLearnerReady()
            }
        }
    }

    private fun postLearnerReady(){
        val subscription = MainManager().postLearnerReady()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(activity.requestErrorHandler(teachers.code(),teachers.message())){
                                currentButton!!.text = getString(R.string.waiting)
                                currentStatus = Constants.STATUS_LEARNER_CONFIRMED
                            }else{
                                Snackbar.make(main_recycler_view_header, "ошибка вышла Симуляция", Snackbar.LENGTH_LONG).show()
                                currentButton!!.text = getString(R.string.waiting)
                                if(currentStatus==Constants.STATUS_TEACHER_CONFIRMED){
                                    ChatActivity.open(activity,currentTeacher!!)
                                }
                                currentStatus = Constants.STATUS_LEARNER_CONFIRMED
                            }
                        },
                        { e ->
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }

    private fun requestLessonToTeacher(teacherId: String , topicId: Int ){
        val subscription = MainManager().postLessonRequest(teacherId,topicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(activity.requestErrorHandler(teachers.code(),teachers.message())){
                                logd(Gson().toJson(teachers.body(), LessonRequestForTeacher::class.java))
                                (list_teachers.adapter as TeachersAdapter).clearOthers()
                                currentButton!!.setText(R.string.waiting)
                                setRealmOnChangeListener()

                            }else{
                                currentStatus = Constants.STATUS_NOT_REQUESTED
                            }
                        },
                        { e ->
                            currentStatus = Constants.STATUS_NOT_REQUESTED
                            Snackbar.make(main_recycler_view_header, e.message ?: "", Snackbar.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                )
        subscriptions.add(subscription)
    }

    fun setRealmOnChangeListener(){
        var request = realm.where(RequestListen::class.java).findFirst()
        realm.executeTransaction {
            if(request==null){
                request = realm.createObject(RequestListen::class.java,0)
            }
            request.status = Constants.STATUS_REQUESTED
            currentStatus = Constants.STATUS_REQUESTED
        }
        val requests = realm.where(RequestListen::class.java).findFirst()
        changeListener = RealmChangeListener {
            rs ->
            if(rs.status==Constants.STATUS_ACCEPTED&&currentStatus==Constants.STATUS_REQUESTED){
                currentStatus = Constants.STATUS_ACCEPTED
                OnTeacherAccepted()
            }else if(rs.status == Constants.STATUS_REQUESTED){
                currentStatus = Constants.STATUS_NOT_REQUESTED
                currentButton!!.setText(R.string.declined)
            }else if(rs.status==Constants.STATUS_TEACHER_CONFIRMED&&currentStatus==Constants.STATUS_LEARNER_CONFIRMED){
                ChatActivity.open(activity,currentTeacher!!)
            }else if(rs.status==Constants.STATUS_TEACHER_CONFIRMED&&currentStatus==Constants.STATUS_ACCEPTED){
                currentStatus = Constants.STATUS_TEACHER_CONFIRMED
            }
        }
        requests.addChangeListener(changeListener)
    }

    private fun OnTeacherAccepted(){
        currentButton?.text = getString(R.string.ready)
        countDownCounter = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if(currentStatus==Constants.STATUS_LEARNER_CONFIRMED){
                    currentButton?.text = getString(R.string.waiting)+getTimeWaitingInMinutes(millisUntilFinished)
                }
            }
            override fun onFinish() {
                currentStatus=Constants.STATUS_NOT_REQUESTED
                currentButton!!.text = getString(R.string.declined)
            }
        }.start()
    }

    private fun getTimeWaitingInMinutes(millis: Long): String
            = String.format(" %dм. %dс.",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    )

    private fun hideFindButton(size: Int){
        number_of_teachers.text = getString(R.string.teachers_accepted).plus(": ").plus(size)
        number_of_teachers.visibility = View.VISIBLE
        find_teacher.visibility = View.GONE
//        topics_bottom.visibility = View.GONE
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

    override fun onDestroy() {
        super.onDestroy()
        countDownCounter?.cancel()
        realm.close()
    }

    companion object {
        val ARG_TOPIC_ITEM = "topicItem"
        val ARG_TEACHER = "teacher"
        fun newInstance(item: TopicItem): TopicFragment {
            val fragment = TopicFragment()
            val bundle = Bundle()
            bundle.putParcelable(ARG_TOPIC_ITEM,item)
            fragment.arguments = bundle
            return fragment
        }
    }
}
