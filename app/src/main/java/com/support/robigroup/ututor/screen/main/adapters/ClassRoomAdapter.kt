package com.support.robigroup.ututor.screen.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.Lesson
import kotlinx.android.synthetic.main.item_lesson.view.*

/**
 * Created by Bimurat Mukhtar on 04.08.2017.
 */
class ClassRoomAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: ArrayList<Lesson> = ArrayList()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as LessonsViewHolder
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LessonsViewHolder(parent)
    }

    override fun getItemCount(): Int = items.size

    fun clearAndAddNews(lessons: List<Lesson>) {
        items.clear()

        items.addAll(lessons)
        logd("${items.size} lessons")
        notifyDataSetChanged()
    }


    class LessonsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_lesson)) {

        fun bind(item: Lesson) = with(itemView) {
            main_lesson_title_textview.text = item.name
        }
    }
}