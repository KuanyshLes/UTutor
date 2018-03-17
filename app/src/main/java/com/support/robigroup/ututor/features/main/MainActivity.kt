package com.support.robigroup.ututor.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.google.gson.Gson
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.NotificationService
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.*
import com.support.robigroup.ututor.commons.Subject
import com.support.robigroup.ututor.features.main.adapters.SubjectsAdapter
import com.support.robigroup.ututor.singleton.SingletonSharedPref
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main_nav.*
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric


class MainActivity :
        AppCompatActivity(),
        OnMainActivityInteractionListener {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private var mSubjectsAdapter: SubjectsAdapter? = null
    private var type = 0
    private var classNumber = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.app_bar_main_nav)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        type = intent.getIntExtra(KEY_TYPE,0)
        supportActionBar?.title = Constants.TYPES.get(type-1).Name
        val profile = SingletonSharedPref.getInstance().getString(Constants.KEY_PROFILE,"")
        if(!profile.equals(""))
            classNumber = Integer.parseInt(Gson().fromJson(SingletonSharedPref.getInstance().getString(Constants.KEY_PROFILE), Profile::class.java).Class)
        else{
            classNumber = 0
        }

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
        ClassesActivity.open(this,item,type)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    private fun requestSubjects(){
        val subscription = MainManager().getSubjects(type)
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
        private val KEY_TYPE = "type"
        fun open(c: Context, type: Int){
            c.startActivity(Intent(c, MainActivity::class.java).putExtra(KEY_TYPE,type))
        }
    }

}
