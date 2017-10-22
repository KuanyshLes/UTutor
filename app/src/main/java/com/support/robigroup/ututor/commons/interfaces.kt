package com.support.robigroup.ututor.commons

import android.support.v4.app.DialogFragment

interface OnLoginActivityInteractionListener {
    fun onSignInButtonClicked(email: String, password: String)
    fun onSignUpTextClicked()
    fun onNextButtonClicked(token: String)
    fun onGetCodeButtonClicked(phone: String)
    fun onUploadPhotoClicked()
    fun onVerifyCodeButtonClicked(token: String)
    fun onSetPasswordButtonClicked(token: String)
    fun getFirstToken(): String
    fun getSecondToken(): String
    fun getPhoneNumber(): String

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