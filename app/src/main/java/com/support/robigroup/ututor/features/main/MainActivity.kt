package com.support.robigroup.ututor.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.commons.Subject
import com.support.robigroup.ututor.features.main.adapters.SubjectsAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main_nav.*


class MainActivity :
        AppCompatActivity(),
        OnMainActivityInteractionListener {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mSubjectsAdapter: SubjectsAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main_nav)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.drawer_item_home_work)

        val intent = Intent()
        intent.setClass(this, NotificationService::class.java)
        startService(intent)

        initAdapters()
        swipe_container.setOnRefreshListener {
            requestSubjects()
        }

        if(Functions.isOnline(this)){
            requestSubjects()
        }else{
            Functions.builtMessageNoInternet(this,{requestSubjects()})
        }
    }

    private fun initAdapters() {
        mSubjectsAdapter = SubjectsAdapter(ArrayList(), this)
        list_subjects.apply {
            setHasFixedSize(true)
            if(adapter == null){
                adapter = mSubjectsAdapter
            }
        }
    }

    override fun onSubjectItemClicked(item: Subject) {
        ClassesActivity.open(this,item)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun requestSubjects(){
        val subscription = MainManager().getSubjects()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedLessons ->
                            if(requestErrorHandler(retrievedLessons.code(),retrievedLessons.message())){
                                mSubjectsAdapter?.updateSubjects(retrievedLessons.body())
                            }
                            if(swipe_container.isRefreshing)
                                swipe_container.isRefreshing = false
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        compositeDisposable.add(subscription)
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
    companion object {
        fun open(c: Context){
            c.startActivity(Intent(c, MainActivity::class.java))
        }
    }

}
