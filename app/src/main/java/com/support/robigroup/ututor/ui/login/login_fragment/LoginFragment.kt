package com.support.robigroup.ututor.ui.login.login_fragment


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpPresenter
import com.support.robigroup.ututor.ui.login.LoginFragmentMvpView
import com.support.robigroup.ututor.ui.login.LoginRegistrationActivityMvpView
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject


class LoginFragment : BaseFragment(), LoginFragmentMvpView {

    @Inject
    lateinit var mPresenter: LoginFragmentMvpPresenter<LoginFragmentMvpView>
    lateinit var mRegistrationActivity: LoginRegistrationActivityMvpView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.fragment_login)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passwordContainer!!.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        signInButton.setOnClickListener { attemptLogin() }
        signUpButton.text = getSpannableText()
        signUpButton.movementMethod = LinkMovementMethod.getInstance()
        signUpButton.highlightColor = Color.TRANSPARENT
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mRegistrationActivity = activity as LoginRegistrationActivityMvpView
    }

    override fun setUp(view: View?) {

    }

    override fun openRegistrationFragment() {
        mRegistrationActivity.replaceRegistrationFragment()
    }

    override fun setIncorrectLoginError(error: String) {
        emailContainer.error = error
    }

    override fun setIncorrectPasswordError(error: String) {
        passwordContainer.error = error
    }

    override fun resetErrors() {
        emailContainer.error = null
        passwordContainer.error = null
    }

    private fun attemptLogin() {
        val emailStr = emailContainer!!.text.toString()
        val passwordStr = passwordContainer!!.text.toString()
        mPresenter.onSignInButtonClicked(emailStr, passwordStr)
    }

    private fun getSpannableText(): SpannableString{
        var textNoAccount = resources.getString(R.string.action_go_sign_up)
        val textSignUp = resources.getString(R.string.action_sign_up)
        val startIndex = textNoAccount.length+1
        textNoAccount = textNoAccount.plus(" ").plus(textSignUp).plus("!")
        val lastIndex = textNoAccount.length
        val ss = SpannableString(textNoAccount)
        ss.setSpan(
                {  _: View ->
                    mPresenter.onSignUpButtonClicked()
                },
                startIndex,
                lastIndex,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ss
    }

    companion object {
        fun newInstance(): LoginFragment {
            val fragment = LoginFragment()
            return fragment
        }
        val TAG = "loginFragment"
    }

}
