package com.support.robigroup.ututor.features.account.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Functions.getLanguages
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.Language
import com.support.robigroup.ututor.singleton.SingletonSharedPref

class LanguagesAdapter() : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: MutableList<Language> = getLanguages()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        holder as LanguagesViewHolder
        val item = items[position]
        holder.itemView.setOnClickListener {
            updateLanguages(position)
        }
        holder.languageText.text = item.text
        holder.checkFlag.visibility = if (item.status) View.VISIBLE  else View.GONE
        holder.flagImage.setImageResource(item.flagIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return LanguagesViewHolder(parent)
    }

    override fun getItemCount(): Int = items.size

    class LanguagesViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
            parent.inflate(R.layout.item_language)) {
        val languageText = itemView.findViewById<TextView>(R.id.text_language)
        val flagImage = itemView.findViewById<ImageView>(R.id.flag_image)
        val checkFlag = itemView.findViewById<ImageView>(R.id.check_flag)
    }

    private fun updateLanguages(pos: Int){
        items.forEach { it.status = false }
        items[pos].status = true
        SingletonSharedPref.getInstance().put(Constants.KEY_LANGUAGE,items[pos].request)
        notifyDataSetChanged()
    }
}