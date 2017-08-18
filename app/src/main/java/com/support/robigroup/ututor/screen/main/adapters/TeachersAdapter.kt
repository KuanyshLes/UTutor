package com.support.robigroup.ututor.screen.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.loadImg
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.screen.chat.ChatActivity
import com.support.robigroup.ututor.screen.main.TopicFragment
import kotlinx.android.synthetic.main.item_teacher.view.*

class TeachersAdapter(fragmentTopic: TopicFragment) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val items: ArrayList<Teacher> = ArrayList()
    val fragment:TopicFragment = fragmentTopic

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


    inner class TeachersViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_teacher)) {

        fun bind(item: Teacher) = with(itemView) {
            teacher_name.text = item.FirstName
            teacher_lessons.text = item.Classes
            teacher_rating.text = item.Raiting.toString()
            teacher_photo.loadImg()
            teacher_choose_button.setOnClickListener {
                fragment.onTeacherItemClicked(item,itemView)
            }
        }
    }
}