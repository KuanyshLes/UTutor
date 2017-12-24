package com.support.robigroup.ututor.features.chat

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.widget.Button
import android.widget.TextView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnChatActivityDialogInteractionListener
import java.util.concurrent.TimeUnit

class ReadyDialog : DialogFragment() {

    var mListener: OnChatActivityDialogInteractionListener? = null
    var mButtonReady: Button? = null
    var mTextWait: TextView? = null
    var mTimer: CountDownTimer? = null

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
        val inflater = activity!!.layoutInflater
        val view = inflater.inflate(R.layout.dialog_ready,null)
        mButtonReady = view.findViewById<Button>(R.id.button_ready) as Button
        mTextWait = view.findViewById<TextView>(R.id.text_ready_time) as TextView
        mButtonReady?.setOnClickListener {
            mListener!!.onReadyDialogReadyClick(this)
        }
        builder.setView(view)
        return builder.create()
    }

    fun startShow(fragmentManager: FragmentManager, tag: String, dif: Long){
        show(fragmentManager,tag)
        mTimer = MyDownTimer(dif)
        mTimer?.start()
    }

    fun updateButtonText(){
        mButtonReady?.text = context!!.getString(R.string.waiting)
    }

    override fun onDestroyView() {
        mTimer?.cancel()
        super.onDestroyView()
    }

    private fun getTimeWaitingInMinutes(millis: Long): String
            = String.format(" %dс.",
            TimeUnit.MILLISECONDS.toSeconds(millis) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
    )

    inner class MyDownTimer(allTimeInMillis: Long): CountDownTimer(allTimeInMillis,1000){

        override fun onFinish() {
            mListener?.onFinishCounterFromReadyDialog()
        }

        override fun onTick(p0: Long) {
            if(mTextWait!=null){
                mTextWait?.text = mTextWait!!.context.getString(R.string.waiting)+getTimeWaitingInMinutes(p0)
            }
        }

    }

}