package com.support.robigroup.ututor.ui.login.login_fragment


import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : BaseFragment() {

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

        signInButton!!.setOnClickListener { attemptLogin() }

        //Editing clickable 'sign up' inside other text
        var accountCheck: String = resources.getString(R.string.action_go_sign_up)
        val signUp: String = resources.getString(R.string.action_sign_up)
        val i1 = accountCheck.length+1
        accountCheck = accountCheck.plus(" ").plus(signUp).plus("!")
        val i2 = accountCheck.length

        val ss = SpannableString(accountCheck)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                mListener!!.onSignUpTextClicked()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
//                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, i1, i2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signUpButton!!.text = ss
        signUpButton!!.movementMethod = LinkMovementMethod.getInstance()
        signUpButton!!.highlightColor = Color.TRANSPARENT
    }

    override fun setUp(view: View?) {

    }

    private fun attemptLogin() {
        val emailStr = emailContainer!!.text.toString()
        val passwordStr = passwordContainer!!.text.toString()
        mListener!!.onSignInButtonClicked(emailStr,passwordStr)


    }

    companion object {
        fun newInstance(param1: String, param2: String): LoginFragment {
            val fragment = LoginFragment()
            return fragment
        }

        val TAG = "loginFragment"
    }

}// Required empty public constructor
