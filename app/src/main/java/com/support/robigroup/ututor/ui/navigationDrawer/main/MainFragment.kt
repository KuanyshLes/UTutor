package com.support.robigroup.ututor.ui.navigationDrawer.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.features.main.MainActivity
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.chat.ActivityChat
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerMvpView
import kotlinx.android.synthetic.main.main_fragment.*
import javax.inject.Inject

class MainFragment : BaseFragment(), MainFragmentMvpView {

    @Inject
    lateinit var mPresenter: MainFragmentMvpPresenter<MainFragmentMvpView>

    private var mListener: DrawerMvpView? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.main_fragment)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        return view
    }

    override fun openChat() {
        ActivityChat.open(baseActivity)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.onViewInitialized()
    }

    override fun setUp(view: View?) {
        mListener?.setActionBarTitle(getString(R.string.choose_type))
        chooseHomeWork.setOnClickListener {
            MainActivity.open(baseActivity,1)
        }
        chooseTest.setOnClickListener {
            MainActivity.open(baseActivity,2)
        }
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
        fun newInstance(): MainFragment {
            return MainFragment()
        }

        const val TAG = "MainFragment"
    }
}
