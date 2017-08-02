package com.support.robigroup.ututor.commons

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

/**
 * Created by Bimurat Mukhtar on 28.07.2017.
 */

fun logd(message: String){
    Log.d("myLogs",message)
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId,this,attachToRoot)
}

fun Context.toast(message: CharSequence,length: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(this, message, length).show()