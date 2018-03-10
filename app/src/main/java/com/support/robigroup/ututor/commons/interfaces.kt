package com.support.robigroup.ututor.commons


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