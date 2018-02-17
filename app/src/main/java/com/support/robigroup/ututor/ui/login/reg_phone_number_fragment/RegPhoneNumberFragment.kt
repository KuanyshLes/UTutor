package com.support.robigroup.ututor.ui.login.reg_phone_number_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.login.*
import kotlinx.android.synthetic.main.fragment_reg_2.*
import javax.inject.Inject

class RegPhoneNumberFragment : BaseFragment(), RegPhoneNumberFragmentView {

    @Inject
    lateinit var mPresenter: RegPhoneNumberFragmentMvpPresenter<RegPhoneNumberFragmentView>
    lateinit var mRegistrationActivity: LoginRegistrationActivityMvpView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_reg_2)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        return view
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mRegistrationActivity = activity as LoginRegistrationActivityMvpView
    }

    override fun setUp(view: View?) {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sendPhoneNumberButton.setOnClickListener {
            val number: String = phone.text.toString()
            mPresenter.onRegisterPhoneButtonClicked(number)
        }
    }

    override fun resetErrors() {
        phone.error = null
    }

    override fun setIncorrectNumberError(error: String?) {
        if(error == null){
            phone.error = getString(R.string.error_phone_number)
        }else{
            phone.error = error
        }
    }

    override fun setEmptyNumberError() {
        phone.error = getString(R.string.error_field_required)
    }

    override fun openVerifyCodeFragment(token: String) {

    }

    companion object {
        fun newInstance(): RegPhoneNumberFragment {
            val fragment = RegPhoneNumberFragment()
            return fragment
        }

        val TAG = "regPhoneFragment"
        val EMAIL_TOKEN = "emailToken"
    }

}
