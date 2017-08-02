package com.support.robigroup.ututor.screen.login


import android.content.Context
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.RelativeLayout
import android.widget.TextView

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.screen.loading.LoadingDialog
import com.support.robigroup.ututor.screen.loading.LoadingView
import kotlinx.android.synthetic.main.fragment_login.*
import kotlin.properties.Delegates


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private var mListener: OnLoginActivityInterationListener? = null
    private var rootLayout: View? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if(rootLayout==null){
            rootLayout = container?.inflate(R.layout.fragment_login)
        }
        return rootLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        passwordContainer.setOnEditorActionListener(TextView.OnEditorActionListener { _, id, _ ->
            if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                attemptLogin()
                return@OnEditorActionListener true
            }
            false
        })

        email_sign_in_button.setOnClickListener { attemptLogin() }

        //Editing clickable 'sign up' inside other text
        var accountCheck: String = resources.getString(R.string.action_go_sign_up)
        val signUp: String = resources.getString(R.string.action_sign_up)
        val i1 = accountCheck.length+1
        accountCheck = accountCheck.plus(" ").plus(signUp).plus("!")
        val i2 = accountCheck.length

        val ss = SpannableString(accountCheck)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                mListener!!.OnSignUpTextClicked()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
//                ds.isUnderlineText = false
            }
        }
        ss.setSpan(clickableSpan, i1, i2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        go_sign_up_button.text = ss
        go_sign_up_button.movementMethod = LinkMovementMethod.getInstance()
        go_sign_up_button.highlightColor = Color.TRANSPARENT
    }
    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */

    fun setEmailError(errEmail: String?): View?{
        emailContainer.error = errEmail
        return emailContainer
    }

    fun setPasswordError(passError: String?): View?{
        passwordContainer.error = passError
        return passwordContainer
    }

    fun resetError(){
        setEmailError(null)
        setPasswordError(null)
    }

    private fun attemptLogin() {
        val emailStr = emailContainer.text.toString()
        val passwordStr = passwordContainer.text.toString()
        mListener!!.OnSignInButtonClicked(emailStr,passwordStr)


    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnLoginActivityInterationListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

}
