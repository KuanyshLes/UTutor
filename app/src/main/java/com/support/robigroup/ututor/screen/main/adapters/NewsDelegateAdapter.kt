package com.support.robigroup.ututor.screen.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.adapter.ViewType
import com.support.robigroup.ututor.commons.adapter.ViewTypeDelegateAdapter
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.model.content.TopicItem
import kotlinx.android.synthetic.main.item_topic.view.*

/**
 * Created by Bimurat Mukhtar on 07.07.2017.
 */
class NewsDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return NewsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as NewsViewHolder
        holder.bind(item as TopicItem)
    }

    class NewsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_topic)) {

        fun bind(item: TopicItem) = with(itemView) {

            main_lesson_title.text = item.lesson
            topic_description_text.text = item.title
            date_create.text = item.created.toString()
            //TODO add time
            topic_rating.text = item.rating.toString()

        }
    }
}