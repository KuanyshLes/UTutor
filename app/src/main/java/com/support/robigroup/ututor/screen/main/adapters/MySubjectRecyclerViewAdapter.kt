package com.support.robigroup.ututor.screen.main.adapters

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.model.content.Subject


class MySubjectRecyclerViewAdapter(private val mValues: ArrayList<Subject>, private val mListener: OnMainActivityInteractionListener?) : RecyclerView.Adapter<MySubjectRecyclerViewAdapter.ViewHolder>() {

    companion object {
        private val colors: Array<String> = arrayOf("#8A4FC6","#A2DE49","#EC102B","#FFB80D","#4A90E2","#43C1A5")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_subject, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.mItem = mValues[position]
        holder.mContentView.text = mValues[position].Text
        holder.mView.setOnClickListener {
            mListener?.onSubjectItemClicked(holder.mItem!!)
        }
        holder.mLeftSquare.setBackgroundColor(Color.parseColor(colors[position%colors.size]))

    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val mContentView: TextView = mView.findViewById<TextView>(R.id.main_lesson_title_text) as TextView
        val mLeftSquare: TextView = mView.findViewById<TextView>(R.id.leftSquare) as TextView
        var mItem: Subject? = null
    }

    fun clearAndAddSubjects(subjects: List<Subject>?) {
        mValues.clear()
        mValues.addAll(subjects!!)
        notifyDataSetChanged()
    }
}
