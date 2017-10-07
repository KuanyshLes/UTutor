package com.support.robigroup.ututor.features.history.adapter

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnHistoryListInteractionListener
import com.support.robigroup.ututor.model.content.ChatHistory
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class HistoryAdapter(
        private val mValues: ArrayList<ChatHistory>,
        private val mListener: OnHistoryListInteractionListener?
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mView.setOnClickListener {
            mListener?.onHistoryItemClicked(holder.mItem)
        }

        holder.mSubjectName.text = String.format(
                "%s",
                holder.mItem.SubjectName
        )
        var date: Date = SimpleDateFormat(Constants.TIMEFORMAT).parse(holder.mItem.EndTime+"Z")
        val myFormat = "yyyy-MM-dd HH:mm"
        holder.mSubjectTime.text = String.format(
                "%s | %s",
                SimpleDateFormat(myFormat).format(date),
                getTimeWaitingInMinutes((holder.mItem.Duration)!!.toInt()*1000L)
        )
        holder.mCostLesson.text = String.format("%s %s", holder.mItem.InvoiceSum,  holder.mView.context.getString(R.string.currency))
        holder.mTeacher.text = String.format("%s", holder.mItem.ChatUserName)
//        Picasso.with(holder.mView.context).load(Constants.BASE_URL+holder.mItem.ChatUserProfilePhoto).into(holder.mTeacherImage )
        Log.e("image",holder.mItem.ChatUserProfilePhoto)
        if(holder.mItem.LearnerRaiting!=null)
            holder.mRating.rating = holder.mItem.LearnerRaiting!!
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var mSubjectName: TextView = mView.findViewById<TextView>(R.id.his_subject_name) as TextView
        var mSubjectTime: TextView = mView.findViewById<TextView>(R.id.his_des) as TextView
        var mTeacher: TextView = mView.findViewById<TextView>(R.id.his_teacher_name) as TextView
        var mTeacherImage: ImageView = mView.findViewById<ImageView>(R.id.his_teacher_image) as ImageView
        var mCostLesson: TextView = mView.findViewById<TextView>(R.id.his_cost) as TextView
        var mRating: RatingBar = mView.findViewById<RatingBar>(R.id.his_rating) as RatingBar
        lateinit var mItem: ChatHistory
    }

    fun updateHistory(subjects: List<ChatHistory>?) {
        mValues.clear()
        mValues.addAll(subjects!!)
        notifyDataSetChanged()
    }

    private fun getTimeWaitingInMinutes(millis: Long): String
            = String.format(" %02d:%02d",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    )


}
