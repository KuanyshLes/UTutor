package com.support.robigroup.ututor.screen.chat

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.widget.Button
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnChatActivityDialogInteractionListener
import com.support.robigroup.ututor.model.content.ChatInformation
import com.support.robigroup.ututor.model.content.ChatLesson


class FinishDialog : DialogFragment() {

    var mListener: OnChatActivityDialogInteractionListener? = null
    var chatLesson: ChatLesson? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as OnChatActivityDialogInteractionListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity!!.toString() + " must implement NoticeDialogListener")
        }

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater

        val view = inflater.inflate(R.layout.dialog_finish,null)
        val buttonEvaluate = view.findViewById<Button>(R.id.button_evaluate) as Button
        buttonEvaluate.setOnClickListener {
            mListener!!.onEvaluateDialogPositiveClick(this)
        }
        builder.setView(view)
        return builder.create()
    }

    fun showMe(chatLesson: ChatLesson,fragmentManager: FragmentManager,t: String){
        this.chatLesson = chatLesson
        show(fragmentManager,t)
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        mListener?.onCancelEvalDialog()
    }


}