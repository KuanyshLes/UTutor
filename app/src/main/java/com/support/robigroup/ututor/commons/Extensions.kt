package com.support.robigroup.ututor.commons


import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import com.squareup.picasso.Picasso
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import okhttp3.ResponseBody
import java.time.Duration

fun logd(message: String,tag: String = "myLogs"){
    Log.d(tag,message)
}

var progressDialog: ProgressDialog? = null

fun Context.builtMessageWait(title: String = getString(R.string.sending), message: String = getString(R.string.wait)) {
    if(progressDialog==null){
        progressDialog = ProgressDialog(applicationContext)
    }
    progressDialog?.setMessage(message)
    progressDialog?.setTitle(title)
    progressDialog?.setCancelable(false)
    progressDialog?.show()
}

fun cancelProgressDialog() {
    if (progressDialog!=null&&progressDialog!!.isShowing()) {
        progressDialog!!.cancel()
    }
}

fun ViewGroup.inflate(layoutId: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutId,this,attachToRoot)
}

fun Context.toast(message: CharSequence,length: Int = Toast.LENGTH_SHORT) =
        Toast.makeText(this, message, length).show()

fun ImageView.loadImg(path: String = "https://www.tes.com/sites/default/files/stress_1.jpg"){
    Picasso.with(context).load(path).into(this)
}

fun SimpleDraweeView.loadSimpleImg(path: String?){
    if(path==null){

    }else{
        setImageURI(path)
    }
}

inline fun <reified T : Parcelable> createParcel(
        crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
            override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
        }

fun Context.isOnline(): Boolean {
    val cm = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val netInfo = cm.activeNetworkInfo
    return netInfo != null && netInfo.isConnectedOrConnecting
}

fun Context.builtMessageNoInternet() {
    val builder = AlertDialog.Builder(applicationContext)
    builder.setMessage("Проверьте интернет соединение!")
            .setCancelable(true)
            .setPositiveButton("OK"){dialog,_ -> dialog.cancel()}
    val alert = builder.create()
    alert.show()
}

fun Activity.requestErrorHandler(code: Int, message: String?, parentView: View = window.decorView.rootView): Boolean {

    if (code in 200 until 300) {
        return true
    }
    if(message!=null){
        val snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_LONG)
        when (code) {
            Constants.BAD_REQUEST -> {
                snackbar.setText("Произошло ошибка, сообщите нам ")
                snackbar.setActionTextColor(Color.RED)
            }
            Constants.UNAUTHORIZED -> {
                snackbar.setText("Ошибка с авторизаций")
                snackbar.setActionTextColor(Color.RED)
            }
            Constants.FORBIDDEN -> {
                snackbar.setText("У вас нет прав для этого")
                snackbar.setActionTextColor(Color.RED)
            }
            Constants.NOT_FOUND -> {
                snackbar.setText("Адрес не нашлось")
                snackbar.setActionTextColor(Color.RED)
            }
            Constants.SERVER_ERROR -> {
                snackbar.setText("Проблемы в сервере повтарите позже")
                snackbar.setActionTextColor(Color.RED)
            }
        }
//        snackbar.show()
    }
    return false
}

