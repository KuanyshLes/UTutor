package com.support.robigroup.ututor.commons

import android.net.Uri
import com.support.robigroup.ututor.model.content.TopicItem

interface OnLoginActivityInteractionListener {
    fun OnSignInButtonClicked (email: String,password: String)
    fun OnSignUpTextClicked ()
    fun OnNextButtonClicked (email: String,password: String,phone: String)
    fun OnDoneButtonClicked (firstName: String,lastName: String)
    fun OnUploadPhotoClicked ()
}
interface OnMainActivityInteractionListener {
    fun OnTopicItemClicked(item: TopicItem)
}