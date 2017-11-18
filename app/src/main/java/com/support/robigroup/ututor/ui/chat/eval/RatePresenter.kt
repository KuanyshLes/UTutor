package com.support.robigroup.ututor.ui.chat.eval

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.RealmBasedPresenter
import com.support.robigroup.ututor.ui.chat.RateMvpPresenter
import com.support.robigroup.ututor.ui.chat.RateMvpView
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject


class RatePresenter<V: RateMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : RealmBasedPresenter<V>(dataManager, schedulerProvider, compositeDisposable), RateMvpPresenter<V>{

    override fun onClickRateButton(rating: Float) {
        compositeDisposable.add(dataManager
                .apiHelper.evalChat(rating.toInt(), chatInformation.Id!!)
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doAfterTerminate {
                    mvpView.hideLoading()
                    mvpView.dismissDialog(Constants.TAG_RATE_DIALOG)
                }
                .doOnSubscribe {
                    mvpView.showLoading()
                }
                .subscribe(
                        { message ->
                            if(message.isSuccessful){
                            }else{
                                handleApiError(ANError(message.raw()))
                            }
                        },
                        { e ->
                            handleApiError(ANError(e))
                        }
                )
        )
    }

    override fun onViewInitialized() {
        mvpView.initViews(chatInformation)
    }
}
