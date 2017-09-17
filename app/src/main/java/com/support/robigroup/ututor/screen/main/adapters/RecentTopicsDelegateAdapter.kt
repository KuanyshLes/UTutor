package com.support.robigroup.ututor.screen.main.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.screen.main.adapters.ViewType
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.main.MainActivity
import kotlinx.android.synthetic.main.item_topic.view.*

/**
 * Created by Bimurat Mukhtar on 07.07.2017.
 */
class RecentTopicsDelegateAdapter : ViewTypeDelegateAdapter {
    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        if(context==null){
            context = parent.context
        }
        return TopicsViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as TopicsViewHolder
        holder.bind(item as TopicItem)
        holder.itemView.setOnClickListener {
//            (context as MainActivity).OnTopicItemClicked(item)
        }
    }

    class TopicsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_topic)) {

        fun bind(item: TopicItem) = with(itemView) {

            main_lesson_title.text = item.Text
            topic_description_text.text = item.Text
            date_create.text = "noBackEnd"
            //TODO add time
            topic_rating.text = "noBackEnd"

        }
    }
}