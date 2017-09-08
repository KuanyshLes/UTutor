package com.support.robigroup.ututor.screen.main.adapters

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.screen.main.adapters.ViewType
import com.support.robigroup.ututor.model.content.TopicItem

class RecentTopicsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: ArrayList<ViewType>
    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
    private val loadingItem = object : ViewType {
        override fun getViewType() = AdapterConstants.LOADING
    }

    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.TOPICS, RecentTopicsDelegateAdapter())
        items = ArrayList()
//        items.add(loadingItem)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return items[position].getViewType()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    fun addRecentTopics(lessons: List<TopicItem>?) {
        // first remove loading and notify
        val initPosition = items.size
//        items.removeAt(initPosition)
//        notifyItemRemoved(initPosition)
        items.addAll(lessons!!)
//        items.add(loadingItem)
        notifyItemRangeChanged(initPosition, items.size /* plus loading item */)
    }

    fun clearAndAddRecentTopics(news: List<TopicItem>?) {
        items.clear()
        items.addAll(news!!)
//        items.add(loadingItem)
        notifyDataSetChanged()
    }

    fun getNews(): List<TopicItem> {
        return items
                .filter { it.getViewType() == AdapterConstants.TOPICS }
                .map { it as TopicItem }
    }
}