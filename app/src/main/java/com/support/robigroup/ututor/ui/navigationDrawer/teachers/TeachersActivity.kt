package com.support.robigroup.ututor.ui.navigationDrawer.teachers

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.support.design.widget.Snackbar
import android.view.MenuItem
import com.google.gson.Gson
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.chat.ActivityChat
import kotlinx.android.synthetic.main.activity_teachers.*
import javax.inject.Inject
import kotlin.properties.Delegates


class TeachersActivity : BaseActivity(), TeachersMvpView, OnTeachersActivityInteractionListener {

    @Inject
    lateinit var mPresenter: TeachersPresenter<TeachersMvpView>
    private var myAdapter: TeachersAdapter by Delegates.notNull()

    companion object {
        const val ARG_SUBJECT = "topicItem"
        const val ARG_ADAPTER = "adapter"
        const val ARG_TYPE = "type"
        fun open(context: Context, item: Subject, type: Int){
            context.startActivity(Intent(context, TeachersActivity::class.java)
                    .putExtra(ARG_SUBJECT,item)
                    .putExtra(ARG_TYPE,type))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_teachers)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        setUp()
        mPresenter.onViewInitialized(intent.getParcelableExtra(ARG_SUBJECT), intent.getIntExtra(ARG_TYPE,0))
        swipeRefreshLayout.setOnRefreshListener {
            mPresenter.onRefreshTeachersList()
        }
    }

    override fun setUp() {
        setSupportActionBar(drawer_toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = getString(R.string.search)
        myAdapter = TeachersAdapter(this)
        if(list_teachers.adapter == null){
            list_teachers.adapter = myAdapter
        }
    }



    override fun onSaveInstanceState(outState: Bundle?, outPersistentState: PersistableBundle?) {
        super.onSaveInstanceState(outState, outPersistentState)

        SingletonSharedPref.getInstance().put(ARG_ADAPTER,Gson().toJson(myAdapter.getTeachers(), Teachers::class.java))
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

    override fun onTeacherChooseClicked(item: Teacher){
        if(item.LessonRequestId!=null){
            Snackbar.make(findViewById(android.R.id.content),getString(R.string.error_request_exists),Snackbar.LENGTH_SHORT)
        }else{
            mPresenter.onChooseButtonClicked(item)
        }
    }

    override fun openChat(){
        ActivityChat.open(this)
    }

    override fun updateTeachersCount(count: Int?) {
        if(count == null){
            number_of_teachers.text = String.format("%s", getString(R.string.teachers_found))
        }else{
            number_of_teachers.text = String.format("%s %d", getString(R.string.teachers_found), count)
        }
    }

    override fun updateAdapterToRequestedState(request: LessonRequestForTeacher) {
        myAdapter.OnRequestedState(request)
    }

    override fun notifyDataChange() {
        myAdapter.notifyDataSetChanged()
    }

    override fun clearAndAddTeachers(teachers: List<Teacher>) {
        myAdapter.clearAndAddTeachers(teachers)
    }

    override fun showErrorNoBalance() {
        Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.error_no_balance),
                Snackbar.LENGTH_LONG
        ).show()
    }

    override fun showErrorRequestExits() {
        Snackbar.make(findViewById(android.R.id.content),
                getString(R.string.error_request_exists),
                Snackbar.LENGTH_LONG
        ).show()
    }

    override fun onCancelRequest(item: Teacher) {
        mPresenter.onCancelRequestButtonClicked(item)
    }

    override fun setRefreshing(isRefresh: Boolean) {
        swipeRefreshLayout.isRefreshing = isRefresh
    }
}
