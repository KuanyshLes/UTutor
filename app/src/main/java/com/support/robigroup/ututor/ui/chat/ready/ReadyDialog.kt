package com.support.robigroup.ututor.ui.chat.ready

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.widget.Button
import android.widget.TextView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.ui.chat.ChatMvpPresenter
import com.support.robigroup.ututor.ui.chat.ChatMvpView
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ReadyDialog : DialogFragment() {

    @Inject
    lateinit var mListener: ChatMvpPresenter<ChatMvpView>
    var mButtonReady: Button? = null
    var mTextWait: TextView? = null
    var mTimer: CountDownTimer? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_ready,null)
        mButtonReady = view.findViewById<Button>(R.id.button_ready) as Button
        mTextWait = view.findViewById<TextView>(R.id.text_ready_time) as TextView
        mButtonReady?.setOnClickListener {
            mListener.onReadyClick()
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
        mButtonReady?.text = context.getString(R.string.waiting)
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
            mListener.onCounterFinish()
        }

        override fun onTick(p0: Long) {
            if(mTextWait!=null){
                mTextWait?.text = String.format("%s %s", mTextWait!!.context.getString(R.string.waiting), getTimeWaitingInMinutes(p0))
            }
        }

    }

}