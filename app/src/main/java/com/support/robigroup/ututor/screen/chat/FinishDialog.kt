package com.support.robigroup.ututor.screen.chat

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.widget.Button
import com.support.robigroup.ututor.R


class FinishDialog : DialogFragment() {

    var mListener: NoticeDialogListener? = null

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
    }

    override fun onAttach(activity: Context?) {
        super.onAttach(activity)
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = activity as NoticeDialogListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(activity!!.toString() + " must implement NoticeDialogListener")
        }

    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_finish,null)
        val buttonFinish = view.findViewById<Button>(R.id.button_finish) as Button
        buttonFinish.setOnClickListener {
            mListener!!.onDialogPositiveClick(this)
        }
        builder.setView(view)
        return builder.create()
    }


}