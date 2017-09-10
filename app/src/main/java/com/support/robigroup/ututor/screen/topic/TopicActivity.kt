package com.support.robigroup.ututor.screen.topic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
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
import com.support.robigroup.ututor.model.content.*
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.screen.main.adapters.RecentTopicsAdapter
import com.support.robigroup.ututor.screen.topic.adapters.TeachersAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.RealmObjectChangeListener
import kotlinx.android.synthetic.main.activity_topic.*
import kotlinx.android.synthetic.main.topics.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class TopicActivity : AppCompatActivity(), OnTopicActivityInteractionListener {

    private var itemTopic: TopicItem by Delegates.notNull()
    private var chatInformation: ChatInformation? = null
    private var myAdapter: TeachersAdapter by Delegates.notNull()
    private var changeListener: RealmChangeListener<ChatInformation>? = null
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

        myAdapter = TeachersAdapter(this)
        if(list_teachers.adapter == null){
            list_teachers.adapter = myAdapter
        }
        if(savedInstanceState!=null){
            itemTopic = savedInstanceState.getParcelable(ARG_TOPIC_ITEM)
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
        if(currentTeacher!!.Status!=Constants.STATUS_NOT_REQUESTED){
            checkUpdates()
        }
        when(currentTeacher!!.Status){
            Constants.STATUS_REQUESTED ->{
                number_of_teachers.visibility = View.GONE
                find_teacher.visibility = View.GONE
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

    private fun checkUpdates(){
        val subscription = MainManager().getChatInformation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(requestErrorHandler(teachers.code(),teachers.message())){
                                logd(Gson().toJson(teachers.body(), ChatLesson::class.java))
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
                                OnLearnerReady(teachers.body()?.charStream()?.readText())
                            }else{
                                myAdapter.OnErrorButton()
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
                        { chatInformation ->
                            if(requestErrorHandler(chatInformation.code(),chatInformation.message())){
                                if(chatInformation.body()!=null){
                                    onSuccessRequestedState(chatInformation.body())
                                }
                            }else{
                                myAdapter.OnNotRequestedState()
                            }
                        },
                        { e ->
                            myAdapter.OnNotRequestedState()
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                            e.printStackTrace()
                        }
                )
        subscriptions.add(subscription)
    }

    //Events
    override fun OnTeacherItemClicked(item: Teacher,view: View){
        when(item.Status){
            Constants.STATUS_NOT_REQUESTED ->{
                myAdapter.OnRequestedState()
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

    //Additional private methods
    private fun onSuccessRequestedState(requestForTeacher: LessonRequestForTeacher? = null){
        (list_teachers.adapter as TeachersAdapter).clearOthers()
        myAdapter.OnRequestedState()
        number_of_teachers.visibility = View.GONE

        if(requestForTeacher!=null){
            val realm = Realm.getDefaultInstance()
            chatInformation = ChatInformation(
                    ClassNumber = requestForTeacher.Class,
                    TopicId = requestForTeacher.TopicId,
                    Learner = requestForTeacher.Learner,
                    TeacherId = requestForTeacher.TeacherId,
                    StatusId = -1,
                    SubjectName = requestForTeacher.SubjectName,
                    TopicTitle = requestForTeacher.TopicTitle,
                    LearnerId = requestForTeacher.LearnerId
            )
            realm.where(ChatInformation::class.java).findAll().deleteAllFromRealm()
            realm.executeTransaction {
                val request = realm.copyToRealm(chatInformation)
                RealmObjectChangeListener<ChatInformation> {
                    rs, changeset ->
                    if(changeset!=null&&!changeset.isDeleted&&rs.StatusId!=Constants.STATUS_REQUESTED_WAIT){
                        if(!rs.TeacherReady&&!rs.LearnerReady){
                            OnTeacherAccepted()
                        }else if(rs.TeacherReady&&rs.LearnerReady){
                            ChatActivity.open(this)
                        }else if(rs.TeacherReady){
                            OnTeacherReady()
                        }else if(rs.LearnerReady){
                            OnLearnerReady(null) //TODO  change this line
                        }
                    }
                }
                request?.addChangeListener(changeListener)
            }
        }
    }

    private fun OnTeacherReady(){
        //TODO teacher ready
        myAdapter.OnTeacherReady()
    }

    private fun OnLearnerReady(res: String?){
        val realm = Realm.getDefaultInstance()
        realm.executeTransaction {
            realm.where(ChatInformation::class.java).findFirst()?.LearnerReady = true
        }
        if(res != null && res.equals("ready")){
            ChatActivity.open(this)
        }else{
            myAdapter.OnLearnerReady()
        }
        realm.close()
    }

    private fun OnTeacherAccepted(){
        myAdapter.OnAcceptedState()
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
