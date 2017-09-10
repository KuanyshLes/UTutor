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
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.Teachers
import kotlinx.android.synthetic.main.item_teacher.view.*

class TeachersAdapter(private val interactionListener: OnTopicActivityInteractionListener) : RecyclerView.Adapter<TeachersAdapter.TeachersViewHolder>() {

    private val items: ArrayList<Teacher> = ArrayList()
    var clickedItemNumber: Int? = null

    override fun onBindViewHolder(holder: TeachersViewHolder, position: Int) {
        holder.bind(items[position])
    }

    fun getTeachers(): Teachers{
        val res = Teachers()
        (res.teachers as ArrayList).addAll(items)
        return res
    }

    fun getRequestedTeacher(initFun: (Teacher,Int) -> Unit): Boolean{
        if(items.size==0) return false
        else{
            for(i in 0 until items.size){
                val item = items[i]
                if(item.Status!=Constants.STATUS_NOT_REQUESTED){
                    initFun(item,i)
                    return true
                }
            }
            return false
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

    fun OnRequestedState(){

    }

    fun OnErrorButton(){

    }

    fun OnNotRequestedState(){

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
                clickedItemNumber = layoutPosition
                interactionListener.OnTeacherItemClicked(item,itemView)
            }
            when(item.Status){
                Constants.STATUS_REQUESTED -> teacher_choose_button.text = itemView.context.getString(R.string.waiting)
                Constants.STATUS_LEARNER_CONFIRMED -> teacher_choose_button.text = itemView.context.getString(R.string.waiting)
                Constants.STATUS_TEACHER_CONFIRMED -> teacher_choose_button.text = itemView.context.getString(R.string.ready)
            }
        }
    }

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearOthers(){
        if(clickedItemNumber!=null){
            logd(items.size.toString()+" "+clickedItemNumber)
            val position: Int = clickedItemNumber!!
            for(i in position+1 until items.size){
                removeAt(position+1)
            }
            if(position!=0)
                for(i in 0 until position){
                    removeAt(0)
                }
        }
    }

}