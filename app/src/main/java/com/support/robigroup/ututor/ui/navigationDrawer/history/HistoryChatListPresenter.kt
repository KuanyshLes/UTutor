package com.support.robigroup.ututor.ui.navigationDrawer.history

import com.androidnetworking.error.ANError
import com.support.robigroup.ututor.data.DataManager
import com.support.robigroup.ututor.ui.base.BasePresenter
import com.support.robigroup.ututor.utils.SchedulerProvider
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

/**
 * Created by Bimurat Mukhtar on 10.03.2018.
 */
class HistoryChatListPresenter<V : ChatsListMvpView> @Inject
constructor(dataManager: DataManager, schedulerProvider: SchedulerProvider, compositeDisposable: CompositeDisposable)
    : BasePresenter<V>(dataManager, schedulerProvider, compositeDisposable), ChatListMvpPresenter<V> {

    override fun onViewInitialized() {
        requestHistory(true)
    }

    override fun onRefreshList() {
        requestHistory(false)
    }

    private fun requestHistory(isFirst: Boolean) {
        compositeDisposable.add(dataManager.apiHelper.getHistory()
                .subscribeOn(schedulerProvider.io())
                .observeOn(schedulerProvider.ui())
                .doOnSubscribe {
                    if(!isFirst)
                        mvpView.setSwipeRefresh(true)
                    else
                        mvpView.showLoading()
                }
                .doAfterTerminate {
                    if(!isFirst)
                        mvpView.setSwipeRefresh(false)
                    else
                        mvpView.hideLoading()
                }
                .subscribe(
                        { retrievedTopics ->
                            if(retrievedTopics.isSuccessful){
                                mvpView.updateChats(retrievedTopics.body())
                            }else{
                                handleApiError(ANError(retrievedTopics.raw()))
                            }
                        },
                        { e ->
                            handleApiError(ANError(e))
                        }
                )
        )
    }
}