package com.support.robigroup.ututor.screen.topic

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import android.view.View
import com.google.gson.Gson
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.OnTeachersActivityInteractionListener
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.commons.snack
import com.support.robigroup.ututor.model.content.*
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.screen.topic.adapters.TeachersAdapter
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_teachers.*
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates

class TeachersActivity : AppCompatActivity(), OnTeachersActivityInteractionListener {
    private var mSubject: Subject by Delegates.notNull()
    private var myAdapter: TeachersAdapter by Delegates.notNull()
    private var subscriptions: CompositeDisposable by Delegates.notNull()
    private var realm: Realm by Delegates.notNull()
    private var chatInfos: RealmResults<ChatInformation> by Delegates.notNull()

    private val mRealmChangeListener: OrderedRealmCollectionChangeListener<RealmResults<ChatInformation>>
            = OrderedRealmCollectionChangeListener { chatInfo, changeSet ->
        if(chatInfo.size>0)
            OnTeacherAccepted(chatInfo[0])
    }

    companion object {

        val ARG_SUBJECT = "topicItem"
        val ARG_ADAPTER = "adapter"
        val EX_LANG = "kk-KZ"
        fun open(context: Context, item: Subject){
            context.startActivity(Intent(context, TeachersActivity::class.java).putExtra(ARG_SUBJECT,item))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teachers)

        realm = Realm.getDefaultInstance()
        chatInfos = realm.where(ChatInformation::class.java).findAll()
        chatInfos.addChangeListener(mRealmChangeListener)

        subscriptions = CompositeDisposable()
        myAdapter = TeachersAdapter(this)
        if(list_teachers.adapter == null){
            list_teachers.adapter = myAdapter
        }
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        mSubject = intent.getParcelableExtra(TeachersActivity.ARG_SUBJECT)

        supportActionBar!!.title = getString(R.string.search)
        number_of_teachers.text = getString(R.string.teachers_found)
        class_text.text = String.format("%d %s",mSubject.ClassNumber,getString(R.string.class_name))
        subject_name.text = mSubject.Text

        requestTeacher(mSubject.ClassNumber, EX_LANG,mSubject.Id)
    }



    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
        SingletonSharedPref.getInstance().put(ARG_ADAPTER,Gson().toJson(myAdapter.getTeachers(),Teachers::class.java))
        chatInfos.removeChangeListener(mRealmChangeListener)
        realm.close()
    }

    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)

        SingletonSharedPref.getInstance().put(ARG_ADAPTER,Gson().toJson(myAdapter.getTeachers(),Teachers::class.java))
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

    //Events
    override fun OnTeacherItemClicked(item: Teacher,view: View){
        if(item.LessonRequestId!=null){
            Snackbar.make(findViewById(android.R.id.content),getString(R.string.error_request_exists),Snackbar.LENGTH_SHORT)
        }else{
            requestLessonToTeacher(item.Id,mSubject.Id, EX_LANG,mSubject.ClassNumber)
        }
//        if(info==null){
//        }else if(info.StatusId==Constants.STATUS_REQUESTED_WAIT){
//            snack(getString(R.string.error_request_exists))
//        }else if(info.StatusId==Constants.STATUS_ACCEPTED_TEACHER&&!info.TeacherReady&&!info.LearnerReady){
//            postLearnerReady()
//        }else if(info.TeacherReady&&!info.LearnerReady){
//            postLearnerReady()
//        }else if(!info.TeacherReady&&info.LearnerReady){
//            snack(getString(R.string.error_request_already_confirmed))
//        }else {
//            snack(getString(R.string.new_case_occur))
//        }
    }


    private fun requestTeacher(classNumber: Int,language: String,subjectId: Int) {
        val subscription = MainManager().getTeachers(classNumber,language,subjectId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { teachers ->
                            if(requestErrorHandler(teachers.code(),teachers.message())){
                                myAdapter.clearAndAddTeachers(teachers.body()!!)
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



    private fun requestLessonToTeacher(teacherId: String, subjectId: Int, language: String, classNumber: Int){
        val subscription = MainManager().postLessonRequest(teacherId, subjectId, language, classNumber)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { chatInformation ->
                            if(requestErrorHandler(chatInformation.code(),chatInformation.message())){
                                if(chatInformation.body()!=null){
                                    onSuccessRequestedState(chatInformation.body()!!)
                                }
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }

    //Additional private methods
    private fun onSuccessRequestedState(requestForTeacher: LessonRequestForTeacher){
        myAdapter.OnRequestedState(requestForTeacher)
    }

    private fun OnTeacherAccepted(info: ChatInformation){
        ChatActivity.open(this)
    }

    private fun getTimeWaitingInMinutes(millis: Long): String
            = String.format(" %dм. %dс.",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    )



}
