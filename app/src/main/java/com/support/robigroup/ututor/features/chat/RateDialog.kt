package com.support.robigroup.ututor.features.chat

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.ui.chat.ActivityChat
import com.support.robigroup.ututor.ui.chat.RateMvpView
import com.support.robigroup.ututor.ui.chat.eval.RatePresenter
import javax.inject.Inject


class RateDialog : DialogFragment() {

    @Inject
    lateinit var mListener: RatePresenter<RateMvpView>
    var chatInformation: ChatInformation? = null
    var ratingBar: RatingBar? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater

        val view = inflater.inflate(R.layout.dialog_finish,null)
        val buttonEvaluate = view.findViewById<Button>(R.id.button_evaluate) as Button
        buttonEvaluate.setOnClickListener {
            mListener.onClickRateButton(ratingBar!!.rating)
            dismiss()
        }
        val textSum = view.findViewById<TextView>(R.id.sum_text) as TextView
        val textDuration = view.findViewById<TextView>(R.id.duration_text) as TextView
        ratingBar = view.findViewById<RatingBar>(R.id.rating_bar) as RatingBar

        textSum.text = String.format("%s₸", chatInformation?.InvoiceSum)
        try {
            textDuration.text = String.format("%s %s",
                    getString(R.string.duration_short),
                    Functions.getTimeWaiting(chatInformation?.Duration?.toLong()!!))
        }catch (e: Exception){
            textDuration.text = Functions.getTimeWaiting(1000)
        }
        builder.setView(view)
        return builder.create()
    }

    fun showMe(chatLesson: ChatInformation, t: String){
        this.chatInformation = chatLesson
        show(activity!!.supportFragmentManager, t)
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        (activity as ActivityChat).startMenuActivity()
    }


}