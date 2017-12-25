package com.support.robigroup.ututor.ui.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.OnHistoryListInteractionListener
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.commons.ChatHistory
import com.support.robigroup.ututor.features.MenuesActivity
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_history_list.*
import android.support.v7.widget.DividerItemDecoration


class HistoryList : MenuesActivity(), OnHistoryListInteractionListener {

    private var mHistoryAdapter: HistoryAdapter? = null
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_list)

        initNav(this)
        updateUI()
        supportActionBar?.title = getString(R.string.history)

        mHistoryAdapter = HistoryAdapter(ArrayList(), this)

        list_history.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@HistoryList, LinearLayoutManager.VERTICAL,false)
            if(adapter == null){
                adapter = mHistoryAdapter
            }
        }

        val dividerItemDecoration = DividerItemDecoration(this,
                LinearLayoutManager.VERTICAL)
        list_history.addItemDecoration(dividerItemDecoration)

        swipeRefreshLayout.setOnRefreshListener {
            requestHistory()
        }

        requestHistory()
    }

    override fun onHistoryItemClicked(item: ChatHistory) {
        HistoryMessages.open(this, item)
    }

    private fun requestHistory() {
        val subscription = MainManager().getHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    if(!swipeRefreshLayout.isRefreshing)
                        swipeRefreshLayout.isRefreshing = true
                }
                .doAfterTerminate {
                    swipeRefreshLayout.isRefreshing = false
                }
                .subscribe(
                        { retrievedTopics ->
                            if (requestErrorHandler(retrievedTopics.code(), retrievedTopics.message())) {
                                mHistoryAdapter?.updateHistory(retrievedTopics.body())
                            }
                        },
                        { e ->
                            Snackbar.make(findViewById(android.R.id.content), e.message ?: "", Snackbar.LENGTH_LONG)
                                    .setAction(getString(R.string.prompt_retry)){
                                        view -> requestHistory()
                                    }.show()
                        }
                )
        compositeDisposable.add(subscription)
    }

    companion object {
        fun open (con: Context){
            con.startActivity(Intent(con, HistoryList::class.java))

        }
    }
}
