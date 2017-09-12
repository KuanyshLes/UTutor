package com.support.robigroup.ututor.screen.topic.adapters

import android.os.CountDownTimer
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnTopicActivityInteractionListener
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.loadImg
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.ChatInformation
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.Teachers
import kotlinx.android.synthetic.main.item_teacher.view.*

class TeachersAdapter(private val interactionListener: OnTopicActivityInteractionListener) : RecyclerView.Adapter<TeachersAdapter.TeachersViewHolder>() {

    private val items: ArrayList<Teacher> = ArrayList()

    override fun onBindViewHolder(holder: TeachersViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getTeachers(): Teachers{
        val res = Teachers()
        (res.teachers as ArrayList).addAll(items)
        return res
    }

    private fun getRequestedTeacherAndPostition(): Pair<Teacher,Int>?{
        if(items.size==0) return null
        else{
            for(i in 0 until items.size){
                val item = items[i]
                if(item.chatInformation!=null&&item.chatInformation?.StatusId!=0){
                    return Pair(item,i)
                }
            }
            return null
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeachersViewHolder {
        return TeachersViewHolder(parent)
    }

    override fun getItemCount(): Int = items.size

    fun clearAndAddTeachers(lessons: List<Teacher>) {
        items.clear()
        items.addAll(lessons)
        notifyDataSetChanged()
    }

    fun OnLearnerReady(){

    }

    fun OnTeacherReady(){

    }

    fun OnAcceptedState(){
//        countDownCounter = object : CountDownTimer(90000, 1000) {
//            override fun onTick(millisUntilFinished: Long) {
//                if(currentTeacher!!.Status==Constants.STATUS_LEARNER_CONFIRMED){
//                    currentButton?.text = getString(R.string.waiting)+getTimeWaitingInMinutes(millisUntilFinished)
//                }
//            }
//            override fun onFinish() {
//                currentTeacher!!.Status=Constants.STATUS_NOT_REQUESTED
//                currentButton!!.text = getString(R.string.declined)
//            }
//        }.start()
    }

    fun OnRequestedState(chatInformation: ChatInformation){
        for(i in 0 until items.size){
            val current = items[i]
            if(current.Id.equals(chatInformation.TeacherId)){
                current.chatInformation = chatInformation
                clearOthers(i)
            }
        }
    }

    fun OnErrorButton(){

    }

    fun OnNotRequestedState(){
        val teacher = getRequestedTeacherAndPostition()
        if(teacher!=null){
            teacher.first.chatInformation = null
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
            teacher_lessons.text = item.Classes
            teacher_rating.text = item.Raiting.toString()
            teacher_photo.loadImg()
            teacher_button_hide.setOnClickListener {
                removeAt(layoutPosition)
            }
            teacher_choose_button.setOnClickListener {
                interactionListener.OnTeacherItemClicked(item,itemView)
            }
            when(item.chatInformation?.StatusId){
                Constants.STATUS_REQUESTED -> teacher_choose_button.text = itemView.context.getString(R.string.waiting)
                Constants.STATUS_LEARNER_CONFIRMED -> teacher_choose_button.text = itemView.context.getString(R.string.waiting)
                Constants.STATUS_TEACHER_CONFIRMED -> teacher_choose_button.text = itemView.context.getString(R.string.ready)
            }
        }
    }
}