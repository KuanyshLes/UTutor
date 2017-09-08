package com.support.robigroup.ututor.screen.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.SignalRService
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.ClassRoom
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.main.adapters.ListViewAdapter
import com.support.robigroup.ututor.screen.topic.TopicActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), OnMainActivityInteractionListener {
    companion object {

        private val TAG_MAIN_FRAGMENT: String = "mainFragment"
    }
    private var stringArrayList:
            MutableList<TopicItem> = MutableList(40, { TopicItem(Id = 0, Text = "math is math is mismath exception") })
    private var adapter: ListViewAdapter? = null
    private val EX_LANG = "kk-KZ"

    override fun onCreate(savedInstanceState: Bundle?) {

        setContentView(R.layout.activity_main)

        val intent = Intent()
        intent.setClass(this, SignalRService::class.java)
        startService(intent)

        setSupportActionBar(toolbar)

        adapter = ListViewAdapter(this, R.layout.item_search, searchList = stringArrayList)
        listview_results!!.adapter = adapter

        listview_results.onItemClickListener = AdapterView.OnItemClickListener { adapterView, _, i, _ ->
            val clickedTopicItem = adapterView.getItemAtPosition(i) as TopicItem
            OnTopicItemClicked(clickedTopicItem)
        }
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        logd("onResume MainActivity")
        if(supportFragmentManager.fragments.size==0){
            supportFragmentManager.beginTransaction().replace(R.id.main_container, MainFragment(),TAG_MAIN_FRAGMENT)
                    .addToBackStack(null).commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as? SearchView
        searchView!!.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                logd("onQueryTextChange "+query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                logd("onQueryTextChange "+newText)
                if (TextUtils.isEmpty(newText)) {
                    adapter!!.filter("")
                    listview_results.clearTextFilter()
                } else {
                    adapter!!.filter(newText)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }


    override fun OnTopicItemClicked(item: TopicItem) {
        TopicActivity.open(this,item)
    }

    override fun OnClassItemClicked(item: ClassRoom) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, SubjectsFragment.newInstance(item.number,EX_LANG))
                .addToBackStack(null).commit()
    }

    override fun OnSubjectItemClicked(item: Subject) {
        supportFragmentManager.beginTransaction().replace(R.id.main_container, SearchFragment.newInstance(item))
                .addToBackStack(null).commit()
    }

    override fun setDisplayHomeAsEnabled(showHomeAsUp: Boolean) {
        supportActionBar!!.setDisplayHomeAsUpEnabled(showHomeAsUp)
    }

    override fun setToolbarTitle(title: String) {
        supportActionBar!!.title = title
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

    override fun onBackPressed() {
        logd("onBackPressed MainActivity" )
        if(supportFragmentManager.backStackEntryCount==1){
            supportFragmentManager.popBackStack()
            finish()
        }else{
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}
