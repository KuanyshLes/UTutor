package com.support.robigroup.ututor.ui.navigationDrawer.account

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.chat.ActivityChat
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerMvpView
import kotlinx.android.synthetic.main.activity_account.*
import javax.inject.Inject

class AccountFragment : BaseFragment(), AccountFragmentMvpView {

    @Inject
    lateinit var mPresenter: AccountFragmentMvpPresenter<AccountFragmentMvpView>

    private var mListener: DrawerMvpView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_account)
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
        mListener?.setActionBarTitle(getString(R.string.drawer_item_settings))
    }

    override fun setUp(view: View?) {
        changePassword.setOnClickListener {
            ChangePasswordActivity.open(baseActivity)
        }
        changeLanguage.setOnClickListener {
            ChangeLanguageActivity.open(baseActivity)
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
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }

        const val TAG = "AccountFragment"
    }
}
