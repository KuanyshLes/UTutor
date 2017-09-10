package com.support.robigroup.ututor.commons

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
    fun OnTopicItemClicked(item: TopicItem)
    fun OnSubjectItemClicked(item: Subject)
    fun OnClassItemClicked(item: ClassRoom)
    fun setDisplayHomeAsEnabled(showHomeAsUp: Boolean)
    fun setToolbarTitle(title: String)
    fun checkChatState()
}

interface OnTopicActivityInteractionListener{
    fun OnTeacherItemClicked(item: Teacher,view: View)
}