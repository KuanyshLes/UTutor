package com.support.robigroup.ututor.ui.login.setPasswordFragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.login.*
import kotlinx.android.synthetic.main.fragment_set_password.*
import javax.inject.Inject

class SetPasswordFragment : BaseFragment(), SetPasswordFragmentView {

    @Inject
    lateinit var mPresenter: SetPasswordFragmentMvpPresenter<SetPasswordFragmentView>
    lateinit var mRegistrationActivity: LoginRegistrationActivityMvpView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_set_password)
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
        setPasswordButton.setOnClickListener {
            val password: String = passwordContainer.text.toString()
            val confirmPassword: String = confirmPasswordContainer.text.toString()
            mPresenter.onSetPasswordButtonClicked(password, confirmPassword)
        }
    }

    override fun resetErrors() {
        passwordContainer.error = null
        confirmPasswordContainer.error = null
    }

    override fun setPasswordError(error: String?) {
        when (error){
            null -> passwordContainer.error = getString(R.string.valid_password_description)
            "" -> passwordContainer.error = getString(R.string.error_field_required)
            else -> passwordContainer.error = error
        }
    }

    override fun setConfirmPasswordError(error: String?) {
        when (error){
            null -> confirmPasswordContainer.error = getString(R.string.valid_password_description)
            "" -> confirmPasswordContainer.error = getString(R.string.error_field_required)
            else -> confirmPasswordContainer.error = error
        }
    }

    override fun setUnmatchedPasswordsError() {
        confirmPasswordContainer.error = getString(R.string.error_not_same_password)
    }

    override fun openMenuActivity() {
        mRegistrationActivity.startMenuActivity()
    }

    companion object {
        fun newInstance(): SetPasswordFragment {
            val fragment = SetPasswordFragment()
            return fragment
        }

        val TAG = "setPasswordFragment"
        val EMAIL_TOKEN = "emailToken"
    }

}
