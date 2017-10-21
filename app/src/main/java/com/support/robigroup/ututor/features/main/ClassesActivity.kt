package com.support.robigroup.ututor.features.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.ClassesActivityListener
import com.support.robigroup.ututor.commons.Subject
import com.support.robigroup.ututor.features.main.adapters.ClassAdapter
import com.support.robigroup.ututor.features.teachers.TeachersActivity
import kotlinx.android.synthetic.main.activity_classes.*
import kotlin.properties.Delegates

class ClassesActivity : AppCompatActivity(), ClassesActivityListener {


    private var mSubject: Subject by Delegates.notNull()
    private var adapter: ClassAdapter by Delegates.notNull()
    private var type = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_classes)

        if (intent != null) {
            type = intent.getIntExtra(ARG_TYPE,0)
            mSubject = intent.getParcelableExtra(ARG_SUBJECT)
        }else if(savedInstanceState!=null){
            mSubject = savedInstanceState.getParcelable(ARG_SUBJECT)
        }
        adapter = ClassAdapter(this,mSubject)

        val recyclerView = findViewById<RecyclerView>(R.id.list)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        setSupportActionBar(toolbar)
        supportActionBar?.title = mSubject.Name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onClassItemClicked(item: Subject) {
        TeachersActivity.open(this,item,type)
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
        val ARG_SUBJECT = "subject"
        val ARG_TYPE = "type"
        fun open(c: Context, item: Subject, type: Int){
            c.startActivity(Intent(c,ClassesActivity::class.java).
                    putExtra(ARG_SUBJECT, item).
                    putExtra(ARG_TYPE, type))
        }
    }
}
