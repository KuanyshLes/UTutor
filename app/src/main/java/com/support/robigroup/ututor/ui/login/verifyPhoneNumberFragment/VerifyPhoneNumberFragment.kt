package com.support.robigroup.ututor.ui.login.verifyPhoneNumberFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.login.*
import kotlinx.android.synthetic.main.fragment_verify_code.*
import javax.inject.Inject

class VerifyPhoneNumberFragment : BaseFragment(), VerifyPhoneNumberFragmentView {

    @Inject
    lateinit var mPresenter: VerifyPhoneNumberFragmentMvpPresenter<VerifyPhoneNumberFragmentView>
    lateinit var mRegistrationActivity: LoginRegistrationActivityMvpView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_verify_code)
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
        verifyPhoneNumberButton.setOnClickListener {
            val code: String = codeContainer.text.toString()
            mPresenter.onVerifyPhoneNumberButtonClicked(code)
        }
    }

    override fun resetErrors() {
        codeContainer.error = null
    }

    override fun setCodeError(error: String?) {
        when(error){
            null -> codeContainer.error = getString(R.string.error_invalid_verify_code)
            "" -> codeContainer.error = getString(R.string.error_field_required)
            else -> codeContainer.error = error
        }
    }

    override fun replaceSetPasswordFragment() {
        mRegistrationActivity.replaceSetPasswordFragment()
    }

    companion object {
        fun newInstance(): VerifyPhoneNumberFragment {
            val fragment = VerifyPhoneNumberFragment()
            return fragment
        }

        val TAG = "verifyPhoneNumberFragment"
        val EMAIL_TOKEN = "emailToken"
    }

}
