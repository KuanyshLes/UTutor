package com.support.robigroup.ututor.screen.main.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.model.content.TopicItem
import com.support.robigroup.ututor.screen.main.MainActivity

import java.util.ArrayList
import java.util.Locale

class ListViewAdapter(private val activity: MainActivity, resource: Int, private val friendList: MutableList<TopicItem> = ArrayList<TopicItem>(), private val searchList:  MutableList<TopicItem>)
    : ArrayAdapter<TopicItem>(activity, resource, friendList) {

    override fun getCount(): Int {
        return friendList.size
    }

    override fun getItem(position: Int): TopicItem? {
        return friendList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val holder: ViewHolder
        val inflater = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // If holder not exist then locate all view from UI file.
        if (convertView == null) {
            // inflate UI from XML file
            convertView = inflater.inflate(R.layout.item_search, parent, false)
            // get all UI view
            holder = ViewHolder(convertView)
            // set tag for holder
            convertView!!.tag = holder
        } else {
            // if holder created, get tag from view
            holder = convertView.tag as ViewHolder
        }
        holder.lessonName.text = getItem(position)!!.lesson
        holder.lessonDesc.text = getItem(position)!!.description

        return convertView
    }

    // Filter method
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        friendList.clear()
        if (charText.length == 0) {
            friendList.clear()
        } else {
            for (s in searchList) {
                if (s.description.contains(charText)) {
                    friendList.add(s)
                }
            }
        }
        notifyDataSetChanged()
    }

    class ViewHolder(v: View) {
        val lessonName: TextView
        val lessonDesc: TextView

        init {
            lessonName = v.findViewById<View>(R.id.lessonName) as TextView
            lessonDesc = v.findViewById<View>(R.id.topicName) as TextView
        }
    }
}