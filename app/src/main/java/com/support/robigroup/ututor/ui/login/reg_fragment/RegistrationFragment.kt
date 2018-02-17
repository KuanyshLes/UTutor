package com.support.robigroup.ututor.ui.login.reg_fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivityMvpView
import com.support.robigroup.ututor.ui.login.RegistrationFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.RegistrationFragmentView
import com.support.robigroup.ututor.ui.login.reg_phone_number_fragment.RegPhoneNumberFragment
import kotlinx.android.synthetic.main.fragment_registration.*
import javax.inject.Inject

class RegistrationFragment : BaseFragment(), RegistrationFragmentView {

    @Inject
    lateinit var mPresenter: RegistrationFragmentMvpPresenter<RegistrationFragmentView>
    lateinit var mRegistrationActivity: LoginRegistrationActivityMvpView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_registration)
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
        nextButton.setOnClickListener{
            val surname = regSurname.text.toString()
            val name = regName.text.toString()
            val email = regEmail.text.toString()
            mPresenter.onRegisterEmailButtonClicked(name, surname, email)
        }
    }

    override fun resetErrors(){
        regSurname.error = null
        regName.error = null
        regEmail.error = null
    }

    override fun setIncorrectEmailError(error: String?) {
        if(error == null){
            regEmail.error = getString(R.string.error_invalid_email)
        }else{
            regEmail.error = error
        }
    }

    override fun setEmptyNameError() {
        regName.error = getString(R.string.error_field_required)
    }

    override fun setEmptySurnameError() {
        regSurname.error = getString(R.string.error_field_required)
    }

    override fun setSurnameError(error: String) {
        regSurname.error = error
    }

    override fun setNameError(error: String) {
        regName.error = error
    }

    override fun openRegPhoneNumberFragment() {
        mRegistrationActivity.replaceRegPhoneNumberFragment()
    }

    companion object {
        fun newInstance(): RegistrationFragment {
            val fragment = RegistrationFragment()
            return fragment
        }
        val TAG = "registrationFragment"
    }

}
