package com.support.robigroup.ututor.screen.main

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.Menu
import android.widget.AdapterView
import android.widget.SearchView
import android.widget.Toast
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.main.adapters.ListViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import android.app.SearchManager
import android.content.Context


class MainActivity : AppCompatActivity(), OnMainActivityInteractionListener {

    companion object {
        private val TAG_MAIN_FRAGMENT: String = "mainFragment"
    }
    private var stringArrayList: MutableList<TopicItem>
    private var adapter: ListViewAdapter? = null
    init {
        stringArrayList = MutableList(40,{TopicItem(description = "math is math is mismath exception",lesson = "Physika")})
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        logd("onCreate MainActivity")
        setSupportActionBar(toolbar)

        adapter = ListViewAdapter(this, R.layout.item_search, searchList = stringArrayList)
        list_item!!.setAdapter(adapter)

        list_item.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            Toast.makeText(this@MainActivity, (adapterView.getItemAtPosition(i) as TopicItem).description, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        logd("onResume MainActivity")
        if(supportFragmentManager.fragments.size==0){
            supportFragmentManager.beginTransaction().replace(R.id.main_container, MainFragment(),TAG_MAIN_FRAGMENT).commit()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as? SearchView
        searchView!!.setSearchableInfo(
                searchManager.getSearchableInfo(componentName))

        searchView!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                logd("onQueryTextChange "+query)
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                logd("onQueryTextChange "+newText)
                if (TextUtils.isEmpty(newText)) {
                    adapter!!.filter("")
                    list_item.clearTextFilter()
                } else {
                    adapter!!.filter(newText)
                }
                return true
            }
        })
        return super.onCreateOptionsMenu(menu)
    }

    override fun OnTopicItemClicked(item: TopicItem) {

        supportFragmentManager.beginTransaction().replace(R.id.main_container, TopicFragment.newInstance(item),
                TAG_MAIN_FRAGMENT).addToBackStack(null).commit()

    }

}
