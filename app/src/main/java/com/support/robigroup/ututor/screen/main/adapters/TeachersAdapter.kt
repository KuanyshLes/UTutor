package com.support.robigroup.ututor.screen.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.loadImg
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.Lesson
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.screen.chat.ChatActivity
import kotlinx.android.synthetic.main.item_teacher.view.*

class TeachersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: ArrayList<Teacher> = ArrayList()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as TeachersViewHolder
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return TeachersViewHolder(parent)
    }

    override fun getItemCount(): Int = items.size

    fun clearAndAddNews(lessons: List<Teacher>) {
        items.clear()

        items.addAll(lessons)
        logd("${items.size} teachers")
        notifyDataSetChanged()
    }


    class TeachersViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_teacher)) {

        fun bind(item: Teacher) = with(itemView) {
            teacher_name.text = item.name
            teacher_lessons.text = item.lessons.joinToString { lesson: Lesson -> lesson.Text.plus(", ") }
            teacher_rating.text = item.rating.toString()
            teacher_photo.loadImg()
            teacher_choose.setOnClickListener {
                ChatActivity.open(itemView.context)
            }
        }
    }
}