package com.support.robigroup.ututor.commons

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso

fun logd(message: String){
    Log.d("myLogs",message)
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId,this,attachToRoot)
}

fun Context.toast(message: CharSequence,length: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(this, message, length).show()

fun ImageView.loadImg(path: String = "https://www.tes.com/sites/default/files/stress_1.jpg"){
    Picasso.with(context).load(path).into(this)
}

inline fun <reified T : Parcelable> createParcel(
        crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
            override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
        }