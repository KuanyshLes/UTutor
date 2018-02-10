package com.support.robigroup.ututor.ui.history

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.facebook.drawee.view.SimpleDraweeView
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnHistoryListInteractionListener
import com.support.robigroup.ututor.commons.ChatHistory
import com.support.robigroup.ututor.commons.Functions
import java.util.*


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
                "%s, %d %s",
                holder.mItem.SubjectName,
                holder.mItem.Class,
                holder.itemView.context.resources.getString(R.string.class_name)
        )
        holder.mSubjectTime.text = String.format(
                "%s %s",
                holder.itemView.context.getString(R.string.duration_short),
                Functions.getTimeWaiting((holder.mItem.Duration)!!.toLong())
        )
        holder.mTeacher.text = String.format("%s", holder.mItem.ChatUserName)
        holder.mTeacherImage.setImageURI(Uri.parse(Constants.BASE_URL+holder.mItem.ChatUserProfilePhoto))

    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var mSubjectName: TextView = mView.findViewById<TextView>(R.id.his_subject_name) as TextView
        var mSubjectTime: TextView = mView.findViewById<TextView>(R.id.his_des) as TextView
        var mTeacher: TextView = mView.findViewById<TextView>(R.id.his_teacher_name) as TextView
        var mTeacherImage: ImageView = mView.findViewById<SimpleDraweeView>(R.id.his_teacher_image)
        lateinit var mItem: ChatHistory
    }

    fun updateHistory(subjects: List<ChatHistory>?) {
        mValues.clear()
        mValues.addAll(subjects!!)
        notifyDataSetChanged()
    }
}
