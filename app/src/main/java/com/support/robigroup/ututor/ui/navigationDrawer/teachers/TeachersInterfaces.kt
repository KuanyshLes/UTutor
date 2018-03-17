package com.support.robigroup.ututor.ui.navigationDrawer.teachers

import com.support.robigroup.ututor.commons.LessonRequestForTeacher
import com.support.robigroup.ututor.commons.Subject
import com.support.robigroup.ututor.commons.Teacher
import com.support.robigroup.ututor.ui.base.MvpPresenter
import com.support.robigroup.ututor.ui.base.MvpView

interface TeachersMvpView: MvpView{
    fun updateTeachersCount(count: Int?)
    fun updateAdapterToRequestedState(request: LessonRequestForTeacher)
    fun notifyDataChange()
    fun setRefreshing(isRefresh: Boolean)
    fun clearAndAddTeachers(teachers: List<Teacher>)
    fun showErrorNoBalance()
    fun showErrorRequestExits()
    fun openChat()
}

interface TeachersMvpPresenter<V : TeachersMvpView>: MvpPresenter<V>{
    fun onChooseButtonClicked(teacher: Teacher)
    fun onCancelRequestButtonClicked(teacher: Teacher)
    fun onRefreshTeachersList()
    fun onViewInitialized(subject: Subject, type: Int)

}