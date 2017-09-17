package com.support.robigroup.ututor.screen.main.adapters

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.model.content.ClassRoom
import com.support.robigroup.ututor.model.content.Subject
import kotlinx.android.synthetic.main.item_class.view.*


class ClassAdapter(private val mListener: OnMainActivityInteractionListener?,private val mSubject: Subject) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val items: List<ClassRoom> = List(12, {index ->  ClassRoom(index)})

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as LessonsViewHolder
        holder.bind(items[position])
        holder.itemView.setOnClickListener {
            mSubject.ClassNumber = items[position].number
            mListener?.OnClassItemClicked(mSubject)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LessonsViewHolder(parent)
    }

    override fun getItemCount(): Int = items.size

    class LessonsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_class)) {

        fun bind(item: ClassRoom) = with(itemView) {
            main_class_name.text = "${item.number} ${itemView.context.getString(R.string.class_name)}"
        }
    }
}