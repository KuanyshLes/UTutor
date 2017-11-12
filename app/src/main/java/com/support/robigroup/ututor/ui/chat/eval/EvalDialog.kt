package com.support.robigroup.ututor.ui.chat.eval

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.ui.chat.ChatMvpPresenter
import com.support.robigroup.ututor.ui.chat.ChatMvpView
import javax.inject.Inject

class EvalDialog : DialogFragment() {

    @Inject
    lateinit var mListener: ChatMvpPresenter<ChatMvpView>
    var chatInformation: ChatInformation? = null
    var ratingBar: RatingBar? = null

    companion object {
        fun newInstance(): EvalDialog{
            return EvalDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater

        val view = inflater.inflate(R.layout.dialog_finish,null)
        val buttonEvaluate = view.findViewById<Button>(R.id.button_evaluate) as Button
        buttonEvaluate.setOnClickListener {
            mListener.onClickEvalButton(ratingBar!!.rating)
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

    fun showMe(fragmentManager: FragmentManager, chatLesson: ChatInformation, t: String){
        this.chatInformation = chatLesson
        show(fragmentManager, t)
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        mListener.onCancelEvalDialog()
    }

}