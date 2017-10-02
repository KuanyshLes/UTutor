package com.support.robigroup.ututor.screen.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.OnHistoryListInteractionListener
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.model.content.ChatHistory
import com.support.robigroup.ututor.screen.history.adapter.HistoryAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history_list.*

class HistoryList : AppCompatActivity(), OnHistoryListInteractionListener {

    private var mHistoryAdapter: HistoryAdapter? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_list)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        mHistoryAdapter = HistoryAdapter(ArrayList(), this)

        list_history.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HistoryList)
            if(adapter == null){
                adapter = mHistoryAdapter
            }
        }

        swipeRefreshLayout.setOnRefreshListener {
            requestHistory()
        }

        requestHistory()
    }

    override fun onHistoryItemClicked(item: ChatHistory) {
        HistoryActivity.open(this,item)
    }

    private fun requestHistory() {
        val subscription = MainManager().getHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe (
                        { retrievedTopics ->
                            if(requestErrorHandler(retrievedTopics.code(),retrievedTopics.message())){
                                mHistoryAdapter?.updateHistory(retrievedTopics.body())
                            }
                            if(swipeRefreshLayout.isRefreshing)
                                swipeRefreshLayout.isRefreshing = false
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
        fun open (con: Context){
            con.startActivity(Intent(con, HistoryList::class.java))

        }
    }
}
