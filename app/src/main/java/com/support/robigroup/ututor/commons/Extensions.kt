package com.support.robigroup.ututor.commons


import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import com.support.robigroup.ututor.Constants
import retrofit2.Response

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

fun Activity.requestErrorHandler(response: Response<String>, parentView: View = window.decorView.rootView): Snackbar? {

    val code: Int = response.code()
    if (code in 200 until 300) {
        return null
    }
    val snackbar = Snackbar.make(parentView, response.message(), Snackbar.LENGTH_LONG)
    when (code) {
        Constants.BAD_REQUEST -> {
            snackbar.setText("Client's data is already exist on server or is invalid")
            snackbar.setActionTextColor(Color.RED)
        }
        Constants.UNAUTHORIZED -> {
            snackbar.setText("Client is not authorized")
            snackbar.setActionTextColor(Color.RED)
        }
        Constants.FORBIDDEN -> {
            snackbar.setText("Forbidden request")
            snackbar.setActionTextColor(Color.RED)
        }
        Constants.NOT_FOUND -> {
            snackbar.setText("API not found")
            snackbar.setActionTextColor(Color.RED)
        }
        Constants.SERVER_ERROR -> {
            snackbar.setText("Server is experiencing problems")
            snackbar.setActionTextColor(Color.RED)
        }
    }
    snackbar.show()
    return snackbar
}
