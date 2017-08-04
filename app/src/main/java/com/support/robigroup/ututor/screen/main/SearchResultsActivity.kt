package com.support.robigroup.ututor.screen.main

import android.app.Activity
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import com.support.robigroup.ututor.commons.logd

class SearchResultsActivity: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent(intent)
    }


    override fun onNewIntent(intent: Intent) {
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {

        if (Intent.ACTION_SEARCH.equals(intent.action)) {
            val query: String = intent.getStringExtra(SearchManager.QUERY)
            logd("Search Results ".plus(query))
        }
    }

}