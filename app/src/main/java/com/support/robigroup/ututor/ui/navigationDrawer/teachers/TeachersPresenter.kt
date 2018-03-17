package com.support.robigroup.ututor.ui.navigationDrawer.teachers

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Subject
import com.support.robigroup.ututor.commons.Teacher
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import io.realm.OrderedRealmCollectionChangeListener
import io.realm.RealmResults
import java.net.HttpURLConnection
import javax.inject.Inject
import kotlin.properties.Delegates


class TeachersPresenter<V : TeachersMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable), TeachersMvpPresenter<V> {

    private lateinit var chatInfos: RealmResults<ChatInformation>
    private var mSubject: Subject by Delegates.notNull()
    private var lessonType = 0
    private val mLanguage = sharedPreferences.getString(Constants.KEY_LANGUAGE, "kk")
    private val mRealmChangeListener: OrderedRealmCollectionChangeListener<RealmResults<ChatInformation>> = OrderedRealmCollectionChangeListener { chatInfo, changeSet ->
        if (chatInfo.size > 0) {
            mvpView.openChat()
        }
    }

    override fun onViewInitialized(subject: Subject, type: Int) {
        chatInfos = realm.where(ChatInformation::class.java).findAll()
        chatInfos.addChangeListener(mRealmChangeListener)

        mSubject = subject
        this.lessonType = type

        onRefreshTeachersList()
    }

    override fun onDetach() {
        super.onDetach()
        chatInfos.removeChangeListener(mRealmChangeListener)
    }

    override fun onChooseButtonClicked(teacher: Teacher) {
        if (teacher.LessonRequestId != null) {
            mvpView.showErrorRequestExits()
        } else {
            val subscription = dataManager.apiHelper.postLessonRequest(teacher.Id, mSubject.Id, mLanguage, mSubject.ClassNumber)
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .doOnSubscribe {
                        mvpView.setRefreshing(true)
                    }
                    .doAfterTerminate {
                        mvpView.setRefreshing(false)
                    }
                    .subscribe(
                            { chatInformation ->
                                if (chatInformation.code() == HttpURLConnection.HTTP_BAD_REQUEST) {
                                    mvpView.showErrorNoBalance()
                                } else if (chatInformation.isSuccessful) {
                                    if (chatInformation.body() != null) {
                                        mvpView.updateAdapterToRequestedState(chatInformation.body()!!)
                                    }
                                } else {
                                    handleApiError(ANError(chatInformation.raw()))
                                }
                            },
                            { e ->
                                handleApiError(ANError(e))
                            }
                    )
            compositeDisposable.add(subscription)
        }
    }

    override fun onRefreshTeachersList() {
        val subscription = dataManager.apiHelper.getTeachers(mSubject.ClassNumber, mLanguage, mSubject.Id, lessonType)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe {
                    mvpView.setRefreshing(true)
                }
                .doAfterTerminate {
                    mvpView.setRefreshing(false)
                }
                .subscribe(
                        { teachers ->
                            if (teachers.isSuccessful && teachers.body()!=null) {
                                mvpView.clearAndAddTeachers(teachers.body()!!)
                                mvpView.updateTeachersCount(teachers.body()!!.size)
                            } else {
                                handleApiError(ANError(teachers.raw()))
                            }
                        },
                        { e ->
                            handleApiError(ANError(e))
                        }
                )
        compositeDisposable.add(subscription)
    }

    override fun onCancelRequestButtonClicked(teacher: Teacher) {
        val subscription = dataManager.apiHelper.postCancelRequest(teacher.LessonRequestId!!)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .subscribe(
                        { teachers ->
                            if (teachers.isSuccessful) {
                                teacher.LessonRequestId = null
                                mvpView.notifyDataChange()
                            } else {
                                handleApiError(ANError(teachers.raw()))
                            }
                        },
                        { e ->
                            handleApiError(ANError(e))
                        }
                )
        compositeDisposable.add(subscription)
    }
}