package com.support.robigroup.ututor.features.main.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.TopicItem
import com.support.robigroup.ututor.features.main.MainActivity

import java.util.ArrayList
import java.util.Locale

class ListViewAdapter(
        private val activity: MainActivity,
        resource: Int,
        private val friendList: MutableList<TopicItem> = ArrayList(),
        private var searchList:  MutableList<TopicItem>,
        private val showEmptyResultsEnabled: Boolean = false)
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
        var convertView1 = convertView
        val holder: ViewHolder
        val inflater = activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        // If holder not exist then locate all view from UI file.
        if (convertView1 == null) {
            // inflate UI from XML file
            convertView1 = inflater.inflate(R.layout.item_search, parent, false)
            // get all UI view
            holder = ViewHolder(convertView1)
            // set tag for holder
            convertView1!!.tag = holder
        } else {
            // if holder created, get tag from view
            holder = convertView1.tag as ViewHolder
        }
        holder.lessonName.text = getItem(position)!!.Text
        holder.lessonDesc.text = getItem(position)!!.Text

        return convertView1
    }

    // Filter method
    fun filter(charText: String) {
        var charText = charText
        charText = charText.toLowerCase(Locale.getDefault())
        friendList.clear()
        if (charText.isEmpty()) {
            if(showEmptyResultsEnabled) friendList.addAll(searchList)
            else friendList.clear()
        } else {
            searchList
                    .asSequence()
                    .filter { it.Text!!.contains(charText) }
                    .mapTo(friendList){ it }
        }
        notifyDataSetChanged()
    }

    fun updateSearchList(newList: MutableList<TopicItem>){
        searchList = newList
    }

    class ViewHolder(v: View) {
        val lessonName: TextView = v.findViewById<View>(R.id.lessonName) as TextView
        val lessonDesc: TextView = v.findViewById<View>(R.id.topicName) as TextView
    }

    fun hideResults(){
        friendList.clear()
        notifyDataSetChanged()
    }
}