package com.support.robigroup.ututor.screen.main.adapters


import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.support.robigroup.ututor.screen.main.adapters.ViewType

interface ViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}