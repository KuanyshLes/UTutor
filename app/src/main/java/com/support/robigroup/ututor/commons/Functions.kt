package com.support.robigroup.ututor.commons

import android.app.ProgressDialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.singleton.SingletonSharedPref

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import com.support.robigroup.ututor.ui.chat.model.ChatMessage
import java.util.concurrent.TimeUnit


object Functions {

    private var progressDialog: ProgressDialog? = null
    val GPS_PROVIDER_REQUEST = 10

    fun getLanguages(): MutableList<Language>{
        val l = SingletonSharedPref.getInstance().getString(Constants.KEY_LANGUAGE)
        return Constants.FLAGS
                .map {
                    if(it.request.equals(l)){
                        it.status = true
                    }
                    it
                }.toMutableList()

    }
    fun getLanguage(request: String): Language {
        var lan = Constants.FLAGS.find { it.request.equals(request) }
        if(lan==null){
            lan = Constants.FLAGS.get(0)
        }
        return lan
    }

    fun builtAlertMessageWithText(message: String, context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }

    fun builtMessageNoInternet(context: Context,func:()-> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Проверьте интернет соединение!")
                .setCancelable(false)
                .setPositiveButton("OK")
                {
                    dialog, id ->
                    dialog.cancel()
                    func()
                }
        val alert = builder.create()
        alert.show()
    }

    fun getDifferenceInMillis(dateString: String): Long{
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat(Constants.DEVICE_TIMEFORMAT, Locale.getDefault())
        val dif = sdf.parse(dateString).time-currentTime.time+Constants.WAIT_TIME
        Log.e("Difference","created: "+dateString + " now: "+sdf.format(currentTime.time)+" dif: "+dif.toString())
        return dif
    }

    fun getDeviceTime():String{
        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat(Constants.DEVICE_TIMEFORMAT, Locale.getDefault())
        return sdf.format(currentTime)
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun getChatInformation(chatInformation: ChatLesson): ChatInformation = ChatInformation(
            chatInformation.Id,
            chatInformation.CreateTime,
            chatInformation.StartTime,
            chatInformation.EndTime,
            chatInformation.StatusId,
            chatInformation.Duration,
            chatInformation.TeacherId,
            chatInformation.LearnerId,
            chatInformation.SubjectName,
            chatInformation.Class,
            chatInformation.Learner,
            chatInformation.Teacher,
            chatInformation.TeacherReady,
            chatInformation.LearnerReady,
            chatInformation.LearnerRaiting,
            chatInformation.TeacherRaiting,
            chatInformation.SubjectId,
            chatInformation.Language,
            chatInformation.InvoiceSum,
            chatInformation.InvoiceTariff
    )

    fun getUnmanagedChatInfo(chatInformation: ChatInformation): ChatInformation {
        if(chatInformation.isManaged)
            return ChatInformation(
                    chatInformation.Id,
                    chatInformation.CreateTime,
                    chatInformation.StartTime,
                    chatInformation.EndTime,
                    chatInformation.StatusId,
                    chatInformation.Duration,
                    chatInformation.TeacherId,
                    chatInformation.LearnerId,
                    chatInformation.SubjectName,
                    chatInformation.ClassNumber,
                    chatInformation.Learner,
                    chatInformation.Teacher,
                    chatInformation.TeacherReady,
                    chatInformation.LearnerReady,
                    chatInformation.LearnerRaiting,
                    chatInformation.TeacherRaiting,
                    chatInformation.SubjectId,
                    chatInformation.Language,
                    chatInformation.InvoiceSum,
                    chatInformation.InvoiceTariff
            )
        else{
            return chatInformation
        }
    }

    fun builtMessageWait(context: Context) {
        progressDialog = ProgressDialog(context)
        progressDialog!!.setMessage("Wait")
        progressDialog!!.setTitle("Sending...")
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    fun cancelProgressDialog() {
        if (progressDialog!!.isShowing) {
            progressDialog!!.cancel()
        }
    }

    fun getEncodedImage(fileName: String): String? {

        var encodedString: String? = null
        try {
            val inputStream = FileInputStream(fileName)
            val bytes: ByteArray
            val buffer = ByteArray(8192)
            var bytesRead: Int
            val output = ByteArrayOutputStream()
            bytesRead = inputStream.read(buffer)
            while (bytesRead != -1) {
                output.write(buffer, 0, bytesRead)
                bytesRead = inputStream.read(buffer)
            }
            bytes = output.toByteArray()
            encodedString = Base64.encodeToString(bytes, Base64.DEFAULT)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return encodedString
    }

    private fun getBitmap(uri: Uri, context: Context) :Bitmap?{

        var input: InputStream? = null
        try {
            val IMAGE_MAX_SIZE = 1200000 // 1.2MP
            val mContentResolver = context.contentResolver
            input = mContentResolver.openInputStream(uri)

            // Decode image size
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(input, null, options)
            input.close()

            var scale = 1
            while (options.outWidth * options.outHeight * (1 / Math.pow(scale.toDouble(), 2.0)) > IMAGE_MAX_SIZE) {
                scale++
            }
            Log.d("myLogs", "scale = " + scale + ", orig-width: " + options.outWidth + ",orig-height: " + options.outHeight)

            var resultBitmap: Bitmap? = null
            input = mContentResolver.openInputStream(uri)

            if (scale > 1) {
                scale--
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                val options = BitmapFactory.Options()
                options.inSampleSize = scale
                resultBitmap = BitmapFactory.decodeStream(input, null, options)

                // resize to desired dimensions
                val  height = resultBitmap.height
                val width = resultBitmap.width
                Log.d("myLogs", "1th scale operation dimenions - width: " + width + ",height: " + height)

                val y = Math.sqrt(IMAGE_MAX_SIZE
                        / (( width.toDouble()) / height))
                val x = (y / height) * width

                val scaledBitmap = Bitmap.createScaledBitmap(resultBitmap,  x.toInt(), y.toInt(), true)
                resultBitmap.recycle()
                resultBitmap = scaledBitmap

                System.gc()
            } else {
                resultBitmap = BitmapFactory.decodeStream(input)
            }
            input.close()
            Log.d("myLogs", "bitmap size - width: " +resultBitmap.width + ", height: " +resultBitmap.height)
            return resultBitmap

        } catch (e: IOException) {
            Log.e("myLogs", e.message,e)
            return null
        }
    }

    fun getTimeWaiting(millis: Long): String{
        val hours = TimeUnit.SECONDS.toHours(millis)
        val minutes = TimeUnit.SECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = millis -TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(millis))
        if(hours == 0L) {
            if (minutes == 0L) {
                return String.format("%2dс.", seconds)
            }
            return String.format("%2dм. %2dc.", minutes, seconds)
        }
        return String.format("%2dч. %2dм. %2dc.", hours, minutes, seconds)
    }

    fun getTimerFromMillis(millis: Long): String{
        val hours = TimeUnit.SECONDS.toHours(millis)
        val minutes = TimeUnit.SECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = millis -TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(millis))
        if(hours == 0L) {
            return String.format("%2d:%2d.", minutes, seconds)
        }
        return String.format("%2d:%2dм:2dc.", hours, minutes, seconds)
    }

    fun getTimerFromMillis(millis: Int): String{
        val millisLong = millis.toLong()
        val hours = TimeUnit.MILLISECONDS.toHours(millisLong)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millisLong) - TimeUnit.HOURS.toMinutes(hours)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millisLong) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisLong))
        if(hours == 0L) {
            return String.format("%02d:%02d", minutes, seconds)
        }
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }

    fun getProgressPercentage(tDur: Long, cDur: Long): Int{
        return 0
    }

    fun hasContentFor(message: ChatMessage, type: Byte): Boolean{
        if(message.filePath != null && message.fileIconPath !=null){
            when (type) {
                Constants.CONTENT_TYPE_IMAGE_TEXT -> {
                    return hasEqualFormat(message.filePath, Constants.IMAGE_TYPES)
                }
                Constants.CONTENT_TYPE_VOICE -> {
                    return hasEqualFormat(message.filePath, Constants.AUDIO_TYPES)
                }
            }
        }
        return false
    }

    private fun hasEqualFormat(input: String, formats: Array<String>): Boolean{
        val last4 = input.substring(input.lastIndexOf(".") + 1)
        return formats.contains(last4)
    }

}
