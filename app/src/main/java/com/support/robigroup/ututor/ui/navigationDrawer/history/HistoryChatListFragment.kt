package com.support.robigroup.ututor.ui.navigationDrawer.history

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.ChatHistory
import com.support.robigroup.ututor.commons.OnHistoryListInteractionListener
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.history.HistoryAdapter
import com.support.robigroup.ututor.ui.history.HistoryChatMessagesActivity
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerMvpView
import kotlinx.android.synthetic.main.fragment_history_list.*
import javax.inject.Inject

class HistoryChatListFragment : BaseFragment(), ChatsListMvpView, OnHistoryListInteractionListener {

    @Inject
    lateinit var mPresenter: ChatListMvpPresenter<ChatsListMvpView>

    lateinit var mHistoryAdapter: HistoryAdapter
    private var mListener: DrawerMvpView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_history_list)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.onViewInitialized()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.onDetach()
    }

    override fun setUp(view: View?) {
        mListener?.setActionBarTitle(getString(R.string.history))
        mHistoryAdapter = HistoryAdapter(ArrayList(), this)
        list_history.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL,false)
            if(adapter == null){
                adapter = mHistoryAdapter
            }
        }

        val dividerItemDecoration = DividerItemDecoration(context,
                LinearLayoutManager.VERTICAL)
        list_history.addItemDecoration(dividerItemDecoration)

        swipeRefreshLayout.setOnRefreshListener {
            mPresenter.onRefreshList()
        }
    }

    override fun setSwipeRefresh(refresh: Boolean) {
        swipeRefreshLayout.isRefreshing = refresh
    }

    override fun updateChats(chats: List<ChatHistory>?) {
        mHistoryAdapter.updateHistory(chats)
    }

    override fun onHistoryItemClicked(item: ChatHistory) {
        HistoryChatMessagesActivity.open(baseActivity, item)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DrawerMvpView) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }



    companion object {
        fun newInstance(): HistoryChatListFragment {
            return HistoryChatListFragment()
        }

        const val TAG = "HistoryChatListFragment"
    }
}
