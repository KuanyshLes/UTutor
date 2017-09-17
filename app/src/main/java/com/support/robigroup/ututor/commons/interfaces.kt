package com.support.robigroup.ututor.commons

import android.support.v4.app.DialogFragment
import android.view.View
import com.support.robigroup.ututor.model.content.ClassRoom
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.model.content.Teacher
import com.support.robigroup.ututor.model.content.TopicItem
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
    fun OnClassItemClicked(item: Subject)
    fun setDisplayHomeAsEnabled(showHomeAsUp: Boolean)
    fun setToolbarTitle(title: String)
    fun checkChatState()
}

interface OnTeachersActivityInteractionListener {
    fun OnTeacherItemClicked(item: Teacher,view: View)
}

interface OnChatActivityDialogInteractionListener {
    fun onFinishDialogPositiveClick(dialog: DialogFragment)
    fun onReadyDialogReadyClick(dialog: DialogFragment)
}