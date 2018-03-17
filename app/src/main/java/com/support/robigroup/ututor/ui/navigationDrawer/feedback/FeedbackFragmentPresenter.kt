package com.support.robigroup.ututor.ui.navigationDrawer.feedback

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class FeedbackFragmentPresenter<V : FeedbackFragmentMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable), FeedbackFragmentMvpPresenter<V> {

    override fun onClickSend(text: String) {
        postFeedback(text)
        mvpView.hideKeyboard()
    }

    private fun postFeedback(text: String) {
        mvpView.setDescriptionError(null)
        if (text.isEmpty()) {
            mvpView.setDescriptionError("")
        } else {
            val subs = dataManager.apiHelper.postFeedback(
                    text,
                    mvpView.getVersionName() + mvpView.getVersionCode(),
                    mvpView.getDeviceName())
                    .subscribeOn(schedulerProvider.io())
                    .observeOn(schedulerProvider.ui())
                    .subscribe({ response ->
                        if (response.isSuccessful) {
                            mvpView.onFeedbackSend()
                        } else {
                            handleApiError(ANError(response.raw()))
                        }
                    }, { error ->
                        handleApiError(ANError(error))
                    })
            compositeDisposable.add(subs)
        }
    }
}