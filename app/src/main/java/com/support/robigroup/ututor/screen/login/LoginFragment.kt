package com.support.robigroup.ututor.screen.login


import android.content.Context
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
import com.support.robigroup.ututor.commons.OnLoginActivityInteractionListener
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.commons.logd
import kotlinx.android.synthetic.main.fragment_login.*


/**
 * A simple [Fragment] subclass.
 */
class LoginFragment : Fragment() {

    private var mListener: OnLoginActivityInteractionListener? = null
    private var rootLayout: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logd("onCreate LoginFragment")
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        logd("onCreateView LoginFragment")
        if(rootLayout==null){
            rootLayout = container?.inflate(R.layout.fragment_login)
        }
        return rootLayout
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logd("onActivityCreated LoginFragment")

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
        logd("onDetach LoginFragment")
        if (context is OnLoginActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnMainActivityInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        logd("onDetach LoginFragment")
        mListener = null
    }

}
