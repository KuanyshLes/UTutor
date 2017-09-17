package com.support.robigroup.ututor.screen.chat

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.widget.Button
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnChatActivityDialogInteractionListener
import com.support.robigroup.ututor.model.content.ChatInformation
import java.text.SimpleDateFormat
import java.util.concurrent.TimeUnit

class ReadyDialog : DialogFragment() {

    var mListener: OnChatActivityDialogInteractionListener? = null
    var time: String? = null
    var mButtonReady: Button? = null

    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as OnChatActivityDialogInteractionListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity!!.toString() + " must implement OnChatActivityDialogInteractionListener")
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_ready,null)
        mButtonReady = view.findViewById<Button>(R.id.button_ready) as Button
        mButtonReady?.setOnClickListener {
            mListener!!.onReadyDialogReadyClick(this)
        }
        builder.setView(view)
        return builder.create()
    }

    fun startShow(fragmentManager: FragmentManager, tag: String, startTime: String){
        show(fragmentManager,tag)
        time = startTime
    }

    fun onLearnerReady(){
        val buttonReady = view?.findViewById<Button>(R.id.button_ready) as Button
        buttonReady.text = context.getString(R.string.waiting)
    }

    private fun getTimeWaitingInMinutes(millis: Long): String
            = String.format(" %dм. %dс.",
            TimeUnit.MILLISECONDS.toMinutes(millis),
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    )

    inner class MyDownTimer(val info: ChatInformation, val button: Button?): CountDownTimer(90000,1000){

        override fun onFinish() {
            val outputFormat = SimpleDateFormat(Constants.TIMEFORMAT)
            val createTimeInMillis = outputFormat.parse(info.CreateTime).time
            info.StatusId= Constants.STATUS_DECLINED
            button?.text = button?.context?.getString(R.string.declined)
        }

        override fun onTick(p0: Long) {
            if(!info.LearnerReady&&button!=null){
                button.text = button.context.getString(R.string.waiting)+getTimeWaitingInMinutes(p0)
            }

        }

    }

}