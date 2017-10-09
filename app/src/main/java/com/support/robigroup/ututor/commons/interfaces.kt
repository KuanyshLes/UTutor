package com.support.robigroup.ututor.commons

import android.support.v4.app.DialogFragment

interface OnLoginActivityInteractionListener {
    fun onSignInButtonClicked(email: String, password: String)
    fun onSignUpTextClicked()
    fun onNextButtonClicked(email: String, password: String, phone: String)
    fun onDoneButtonClicked(firstName: String, lastName: String)
    fun onUploadPhotoClicked()
}
interface OnMainActivityInteractionListener {
    fun onSubjectItemClicked(item: Subject)
}

interface OnHistoryListInteractionListener {
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
    fun onEvaluateDialogPositiveClick(rating: Float)
    fun onReadyDialogReadyClick(dialog: DialogFragment)
    fun onFinishCounterFromReadyDialog()
    fun onCancelEvalDialog()
}