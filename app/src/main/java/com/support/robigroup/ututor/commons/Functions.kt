package com.support.robigroup.ututor.commons

import android.app.ProgressDialog
import android.content.Context
import android.net.ConnectivityManager
import android.support.v7.app.AlertDialog
import android.util.Base64
import android.util.Log
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.model.content.ChatInformation
import com.support.robigroup.ututor.model.content.ChatLesson
import com.support.robigroup.ututor.features.chat.model.*

import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object Functions {

    private var progressDialog: ProgressDialog? = null
    val GPS_PROVIDER_REQUEST = 10



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
        val sdf = SimpleDateFormat(Constants.TIMEFORMAT)

        val dif = sdf.parse(dateString+"Z").time-currentTime.time+Constants.WAIT_TIME
        Log.e("Difference","created: "+dateString+"Z"+ " now: "+sdf.format(currentTime.time)+" dif: "+dif.toString())
        return dif
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun getChatInformation(chatInformation: ChatLesson): ChatInformation =  ChatInformation(
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

    fun getMyMessage(message: CustomMessage,user: User): MyMessage {
        if(message.File!=null&&message.FileThumbnail!=null){
            val myMessage = CustomMessage(message.Id,message.Time, Constants.BASE_URL+message.FileThumbnail,
                    Constants.BASE_URL+message.File,message.Message)
            return MyMessage(myMessage,user)
        }else{
            val myMessage = CustomMessage(message.Id,message.Time,Message = message.Message)
            return MyMessage(myMessage,user)
        }
    }

    fun getMyMessageHistory(message: CustomMessageHistory): MyHistoryMessage {
        if(message.FilePath!=null||message.FileOpenIcon!=null){
            val myMessage = CustomMessageHistory(message.Id,message.Time, Constants.BASE_URL+message.FileOpenIcon,
                    Constants.BASE_URL+message.FilePath,message.Owner,message.Text)
            return MyHistoryMessage(myMessage)
        }else{
            return MyHistoryMessage(message)
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
}
