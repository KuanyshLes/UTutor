package com.support.robigroup.ututor.screen.teachers.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnTeachersActivityInteractionListener
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.loadImg
import com.support.robigroup.ututor.model.content.LessonRequestForTeacher
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.Teachers
import kotlinx.android.synthetic.main.item_teacher.view.*
import java.util.*
import java.util.concurrent.TimeUnit

class TeachersAdapter(private val interactionListener: OnTeachersActivityInteractionListener) : RecyclerView.Adapter<TeachersAdapter.TeachersViewHolder>() {

    private val items: ArrayList<Teacher> = ArrayList()

    override fun onBindViewHolder(holder: TeachersViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getTeachers(): Teachers{
        val res = Teachers()
        (res.teachers as ArrayList).addAll(items)
        return res
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeachersViewHolder {
        return TeachersViewHolder(parent)
    }

    override fun getItemCount(): Int = items.size

    fun clearAndAddTeachers(lessons: List<Teacher>) {
        items.clear()
        if(lessons.isNotEmpty())
            items.addAll(lessons)
        notifyDataSetChanged()
    }

    fun OnRequestedState(requestForTeacher: LessonRequestForTeacher){
        for(i in 0 until items.size){
            val current = items[i]
            if(current.Id.equals(requestForTeacher.TeacherId)){
                current.LessonRequestId = requestForTeacher.Id
                notifyItemChanged(i)
                break
            }
        }
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearOthers(position: Int){
        for(i in position+1 until items.size){
            removeAt(position+1)
        }
        if(position!=0)
            for(i in 0 until position){
                removeAt(0)
            }
    }

    inner class TeachersViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_teacher)) {

        fun bind(item: Teacher) = with(itemView) {
            teacher_name.text = item.FirstName
            teacher_lessons.text = item.Speciality ?: "Әлі істелмеген"
            teacher_rating.text = item.Raiting.toString()
            teacher_photo.loadImg()
            teacher_button_hide.setOnClickListener {
                if(item.LessonRequestId==null){
                    removeAt(layoutPosition)
                }else{
                    interactionListener.onCancelRequest(item)
                }

            }
            teacher_choose_button.setOnClickListener {
                interactionListener.onTeacherItemClicked(item)
            }
            if(item.LessonRequestId==null){
                teacher_choose_button.text = itemView.context.getString(R.string.choose_teacher)
                teacher_button_hide.text = itemView.context.getString(R.string.action_hide)
            }else{
                teacher_choose_button.text = itemView.context.getString(R.string.waiting)
                teacher_button_hide.text = itemView.context.getString(R.string.action_cancel)
            }
        }
    }

    private fun getTimeWaitingInMinutes(millis: Long): String
            = String.format(" %dм. %dс.",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    )
}