package com.support.robigroup.ututor.screen.topic.adapters

import android.os.CountDownTimer
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Button
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnTopicActivityInteractionListener
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.loadImg
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.ChatInformation
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.Teachers
import io.realm.Realm
import kotlinx.android.synthetic.main.item_teacher.view.*
import java.util.*
import java.util.concurrent.TimeUnit

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
        if(items.size==1){
            val pair = getRequestedTeacherAndPostition()!!
            Realm.getDefaultInstance().executeTransaction {
                pair.first.chatInformation?.LearnerReady = true
            }
            notifyItemChanged(pair.second)
        }
    }

    fun OnTeacherReady(info: ChatInformation){
        if(items.size==1){
            val pair = getRequestedTeacherAndPostition()!!
            pair.first.chatInformation = info
            notifyItemChanged(pair.second)
        }
    }

    fun OnAcceptedState(info: ChatInformation){
        if(items.size==1){
            val pair = getRequestedTeacherAndPostition()!!
            pair.first.chatInformation = info
            notifyItemChanged(pair.second)
        }
    }

    fun OnRequestedState(chatInformation: ChatInformation){
        for(i in 0 until items.size){
            val current = items[i]
            if(current.Id.equals(chatInformation.TeacherId)){
                current.chatInformation = chatInformation
                clearOthers(i)
                notifyItemChanged(0)
                break
            }
        }
    }

    fun OnErrorButton(){
        if(items.size==1){
            val pair = getRequestedTeacherAndPostition()!!
            Realm.getDefaultInstance().executeTransaction {
                pair.first.chatInformation?.StatusId = Constants.STATUS_ERROR
            }
            notifyItemChanged(pair.second)
        }
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
            if(item.chatInformation==null){
                teacher_choose_button.text =  itemView.context.getString(R.string.choose_teacher)
            }else{
                val info = item.chatInformation!!
                if(info.StatusId==Constants.STATUS_NOT_REQUESTED){
                    teacher_choose_button.text =  itemView.context.getString(R.string.choose_teacher)
                }else if(info.StatusId==Constants.STATUS_REQUESTED_WAIT){
                    teacher_choose_button.text =  itemView.context.getString(R.string.waiting)
                }else if(info.StatusId==Constants.STATUS_ACCEPTED_TEACHER&&!info.LearnerReady){
                    teacher_choose_button.text =  itemView.context.getString(R.string.ready)
                }else if(info.StatusId==Constants.STATUS_ACCEPTED_TEACHER&&info.LearnerReady){
                    MyDownTimer(info,teacher_choose_button).start()
                }else if(info.StatusId==Constants.STATUS_DECLINED){
                    teacher_choose_button.text =  itemView.context.getString(R.string.declined)
                }else {
                    teacher_choose_button.text =  itemView.context.getString(R.string.error)
                }
            }
        }
    }

    private fun getTimeWaitingInMinutes(millis: Long): String
            = String.format(" %dм. %dс.",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    )

    inner class MyDownTimer(val info: ChatInformation,val button: Button?):CountDownTimer(90000,1000){

        override fun onFinish() {
//            val outputFormat = SimpleDateFormat(TIMEFORMAT)
//            outputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//            val createTimeInMillis = outputFormat.parse(info.CreateTime).time
            info.StatusId=Constants.STATUS_DECLINED
            button?.text = button?.context?.getString(R.string.declined)
        }

        override fun onTick(p0: Long) {
            if(!info.LearnerReady&&button!=null){
                button.text = button.context.getString(R.string.waiting)+getTimeWaitingInMinutes(p0)
            }

        }

    }
}