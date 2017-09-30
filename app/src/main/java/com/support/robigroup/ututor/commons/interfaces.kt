package com.support.robigroup.ututor.commons

import android.support.v4.app.DialogFragment
import android.view.View
import com.support.robigroup.ututor.model.content.*
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.screen.main.adapters.ListViewAdapter

interface OnLoginActivityInteractionListener {
    fun onSignInButtonClicked(email: String, password: String)
    fun onSignUpTextClicked()
    fun onNextButtonClicked(email: String, password: String, phone: String)
    fun onDoneButtonClicked(firstName: String, lastName: String)
    fun onUploadPhotoClicked()
}
interface OnMainActivityInteractionListener {
    fun onSubjectItemClicked(item: Subject)
    fun onHistoryItemClicked(item: ChatHistory)
}

interface ClassesActivityListener{
    fun onClassItemClicked(item: Subject)

}

interface OnTeachersActivityInteractionListener {
    fun onTeacherItemClicked(item: Teacher)
    fun onCancelRequest(item: Teacher)
}

interface OnChatActivityDialogInteractionListener {
    fun onEvaluateDialogPositiveClick(dialog: DialogFragment)
    fun onReadyDialogReadyClick(dialog: DialogFragment)
    fun onFinishCounterFromReadyDialog()
    fun onCancelEvalDialog()
}