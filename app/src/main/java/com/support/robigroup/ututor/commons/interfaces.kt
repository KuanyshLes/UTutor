package com.support.robigroup.ututor.commons

import android.support.v4.app.DialogFragment
import android.view.View
import com.support.robigroup.ututor.model.content.*
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.screen.main.adapters.ListViewAdapter

interface OnLoginActivityInteractionListener {
    fun OnSignInButtonClicked (email: String,password: String)
    fun OnSignUpTextClicked ()
    fun OnNextButtonClicked (email: String,password: String,phone: String)
    fun OnDoneButtonClicked (firstName: String,lastName: String)
    fun OnUploadPhotoClicked ()
}
interface OnMainActivityInteractionListener {
    fun OnSubjectItemClicked(item: Subject)
    fun setDisplayHomeAsEnabled(showHomeAsUp: Boolean)
    fun setToolbarTitle(title: String)
}

interface ClassesActivityListener{
    fun OnClassItemClicked(item: Subject)

}

interface OnTeachersActivityInteractionListener {
    fun OnTeacherItemClicked(item: Teacher,view: View)
}

interface OnChatActivityDialogInteractionListener {
    fun onEvaluateDialogPositiveClick(dialog: DialogFragment)
    fun onReadyDialogReadyClick(dialog: DialogFragment)
    fun onFinishCounterFromReadyDialog()
    fun onCancelEvalDialog()
}