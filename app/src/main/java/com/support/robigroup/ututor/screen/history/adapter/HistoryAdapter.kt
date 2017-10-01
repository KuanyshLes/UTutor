package com.support.robigroup.ututor.screen.history.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.model.content.ChatHistory


class HistoryAdapter(
        private val mValues: ArrayList<ChatHistory>,
        private val mListener: OnMainActivityInteractionListener?
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_history_chat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mView.setOnClickListener {
            mListener?.onHistoryItemClicked(holder.mItem)
        }
        holder.mClassNumber.text = String.format("%s", holder.mItem.SubjectName)
        holder.mClassNumber.text = String.format("%s %s", holder.mItem.Class, holder.mView.context.getString(R.string.class_name))
        holder.mCostLesson.text = String.format("%s %s", holder.mItem.InvoiceSum,  holder.mView.context.getString(R.string.currency))
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        var mSubjectName: TextView = mView.findViewById<TextView>(R.id.subject_name) as TextView
        var mClassNumber: TextView = mView.findViewById<TextView>(R.id.class_number) as TextView
        var mCostLesson: TextView = mView.findViewById<TextView>(R.id.lesson_cost) as TextView
        lateinit var mItem: ChatHistory
    }

    fun updateHistory(subjects: List<ChatHistory>?) {
        mValues.clear()
        mValues.addAll(subjects!!)
        notifyDataSetChanged()
    }
}
