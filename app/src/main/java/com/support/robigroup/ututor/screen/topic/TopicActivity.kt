package com.support.robigroup.ututor.screen.topic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.google.gson.Gson
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.OnTopicActivityInteractionListener
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.model.content.RequestListen
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.Teachers
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.screen.main.adapters.RecentTopicsAdapter
import com.support.robigroup.ututor.screen.topic.adapters.TeachersAdapter
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmChangeListener
import kotlinx.android.synthetic.main.activity_topic.*
import kotlinx.android.synthetic.main.topics.*
import microsoft.aspnet.signalr.client.hubs.HubConnection
import microsoft.aspnet.signalr.client.hubs.HubProxy
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class TopicActivity : AppCompatActivity(), OnTopicActivityInteractionListener {

    private var itemTopic: TopicItem by Delegates.notNull()
    private var currentTeacher: Teacher? = null
    private var currentButton: Button? = null
    private var countDownCounter: CountDownTimer? = null
    private var myAdapter: TeachersAdapter by Delegates.notNull()
    private var changeListener: RealmChangeListener<RequestListen>? = null
    private var realm: Realm by Delegates.notNull()
    private var subscriptions: CompositeDisposable by Delegates.notNull()

    companion object {
        val ARG_TOPIC_ITEM = "topicItem"
        val ARG_TEACHER = "teacher"
        val ARG_ADAPTER = "adapter"
        fun open(context: Context, item: TopicItem){
            val intent = Intent()
            context.startActivity(Intent(context, TopicActivity::class.java).putExtra(ARG_TOPIC_ITEM,item))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_topic)

        subscriptions = CompositeDisposable()
        realm = Realm.getDefaultInstance()

        myAdapter = TeachersAdapter(this)
        if(list_teachers.adapter == null){
            list_teachers.adapter = myAdapter
        }
        if(savedInstanceState!=null){
            itemTopic = savedInstanceState.getParcelable(ARG_TOPIC_ITEM)
            val tcs = savedInstanceState.getParcelable<Teachers>(ARG_ADAPTER).teachers
            myAdapter.clearAndAddTeachers(savedInstanceState.getParcelable<Teachers>(ARG_ADAPTER).teachers)
            myAdapter.getRequestedTeacher({teacher, i -> initFun(teacher,i) })
        }else{
            itemTopic = intent.getParcelableExtra(ARG_TOPIC_ITEM)
        }

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = itemTopic.subject?.Text ?: "error"
        topic_desc.text = itemTopic.Text
        class_text.text = "${itemTopic.subject?.classNumber} ${getString(R.string.group)}"
        find_teacher.setOnClickListener {
            requestTeacher()
        }
//        if (main_recycler_view_header.adapter == null) {
//            main_recycler_view_header.adapter = RecentTopicsAdapter()
//        }
    }

    private fun initFun(teacher: Teacher,position: Int){
        currentTeacher = teacher
        Handler().postDelayed(
                {
                    currentButton = list_teachers.findViewHolderForAdapterPosition(position).itemView!!.findViewById(R.id.teacher_choose_button)
                },20
        )
        when(currentTeacher!!.Status){
            Constants.STATUS_REQUESTED ->{
                number_of_teachers.visibility = View.GONE
                find_teacher.visibility = View.GONE
                setRealmOnChangeListener()
            }
            Constants.STATUS_LEARNER_CONFIRMED ->{

            }
            Constants.STATUS_TEACHER_CONFIRMED ->{

            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putParcelable(ARG_TOPIC_ITEM, itemTopic)
        outState?.putParcelable(ARG_ADAPTER,myAdapter.getTeachers())
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
        countDownCounter?.cancel()
        realm.close()
    }

    //WebRequests
    private fun requestSameTopics(subjectId: Int) {
        val subscription = MainManager().getTopics(subjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { retrievedTopics ->
                            if(requestErrorHandler(retrievedTopics.code(),retrievedTopics.message())){
                                (main_recycler_view_header.adapter as RecentTopicsAdapter).clearAndAddRecentTopics(retrievedTopics.body())
                            }else{
                                //TODO handle errors
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
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
                            if(requestErrorHandler(teachers.code(),teachers.message())){
                                myAdapter.clearAndAddTeachers(teachers.body()!!)
                                hideFindButton(teachers.body()!!.size)
                                logd(Gson().toJson(teachers.body()!![0],Teacher::class.java))
                            }else{
                                //TODO handle server errors
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                )
        subscriptions.add(subscription)
    }

    private fun postLearnerReady(){
        val subscription = MainManager().postLearnerReady()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(requestErrorHandler(teachers.code(),teachers.message())){
                                if(currentTeacher!!.Status==Constants.STATUS_TEACHER_CONFIRMED){
                                    ChatActivity.open(this,currentTeacher!!)

                                }else{
                                    currentButton!!.text = getString(R.string.waiting)
                                    currentTeacher!!.Status = Constants.STATUS_LEARNER_CONFIRMED
                                }
                            }else{
                                currentButton!!.text = getString(R.string.error)
                                if(currentTeacher!!.Status==Constants.STATUS_TEACHER_CONFIRMED){
                                    ChatActivity.open(this,currentTeacher!!)
                                }
                                currentTeacher!!.Status = Constants.STATUS_LEARNER_CONFIRMED
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
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
                            if(requestErrorHandler(teachers.code(),teachers.message())){
                                (list_teachers.adapter as TeachersAdapter).clearOthers()
                                currentButton!!.setText(R.string.waiting)
                                number_of_teachers.visibility = View.GONE
                                setRealmOnChangeListener()
                            }else{
                                currentTeacher!!.Status = Constants.STATUS_NOT_REQUESTED
                            }
                        },
                        { e ->
                            currentTeacher!!.Status = Constants.STATUS_NOT_REQUESTED
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                )
        subscriptions.add(subscription)
    }


    //Events
    override fun OnTeacherItemClicked(item: Teacher,itemView: View){
        when(item.Status){
            Constants.STATUS_NOT_REQUESTED ->{
                currentTeacher = item
                currentTeacher!!.Status = Constants.STATUS_REQUESTED
                currentButton = itemView.findViewById<Button>(R.id.teacher_choose_button) as Button
                requestLessonToTeacher(item.Id,itemTopic.Id!!)
            }
            Constants.STATUS_REQUESTED ->{
                Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_request_exists), Snackbar.LENGTH_LONG).show()
            }
            Constants.STATUS_ACCEPTED->{
                postLearnerReady()
            }
            Constants.STATUS_TEACHER_CONFIRMED->{
                postLearnerReady()
            }
            Constants.STATUS_LEARNER_CONFIRMED->{
                Snackbar.make(findViewById(android.R.id.content),getString(R.string.error_request_exists), Snackbar.LENGTH_LONG).show()
            }
        }
    }

    private fun OnTeacherAccepted(){
        currentButton?.text = getString(R.string.ready)
        countDownCounter = object : CountDownTimer(90000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if(currentTeacher!!.Status==Constants.STATUS_LEARNER_CONFIRMED){
                    currentButton?.text = getString(R.string.waiting)+getTimeWaitingInMinutes(millisUntilFinished)
                }
            }
            override fun onFinish() {
                currentTeacher!!.Status=Constants.STATUS_NOT_REQUESTED
                currentButton!!.text = getString(R.string.declined)
            }
        }.start()
    }

    //Additional private methods
    private fun setRealmOnChangeListener(){
        var request = realm.where(RequestListen::class.java).findFirst()
        realm.executeTransaction {
            if(request==null){
                request = realm.createObject(RequestListen::class.java,0)
            }
            request.status = Constants.STATUS_REQUESTED
            currentTeacher!!.Status = Constants.STATUS_REQUESTED
        }
        val requests = realm.where(RequestListen::class.java).findFirst()
        changeListener = RealmChangeListener {
            rs ->
            if(rs.status==Constants.STATUS_ACCEPTED&&currentTeacher!!.Status==Constants.STATUS_REQUESTED){
                currentTeacher!!.Status = Constants.STATUS_ACCEPTED
                OnTeacherAccepted()
            }else if(rs.status==Constants.STATUS_TEACHER_CONFIRMED&&currentTeacher!!.Status==Constants.STATUS_LEARNER_CONFIRMED){
                ChatActivity.open(this,currentTeacher!!)
            }else if(rs.status== Constants.STATUS_TEACHER_CONFIRMED&&currentTeacher!!.Status==Constants.STATUS_ACCEPTED){
                currentTeacher!!.Status = Constants.STATUS_TEACHER_CONFIRMED
            }
        }
        requests.addChangeListener(changeListener)
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

}
