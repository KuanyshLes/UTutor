package com.support.robigroup.ututor.features.login


import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.support.robigroup.ututor.Constants

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.OnLoginActivityInteractionListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_set_password.*
import org.json.JSONObject
import java.util.regex.Matcher
import java.util.regex.Pattern


class SetPasswordFragment : Fragment() {

    lateinit var mListener: OnLoginActivityInteractionListener
    private val pattern = Pattern.compile(Constants.PASSWORD_PATTERN)
    private var matcher: Matcher? = null



    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_set_password, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_set_password.setOnClickListener {
            validatePassword()
        }
    }

    private fun validatePassword(password: String): Boolean {
        matcher = pattern.matcher(password)
        return matcher!!.matches()
    }

    private fun validatePassword(){
        val passwordString: String = password.text.toString()
        val confirmString: String = confirmPassword.text.toString()

        if (!validatePassword(passwordString)) {
            password.error = getString(R.string.desc_password)
            return
        }
        if (!validatePassword(confirmString)) {
            confirmPassword.error = getString(R.string.desc_password)
            return
        }
        if(!passwordString.equals(confirmString)){
            confirmPassword.error = getString(R.string.error_not_same_password)
            return
        }

        RestAPI.getApi().password(passwordString,  mListener.getSecondToken())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    response ->
                    if (response.isSuccessful) {
                        try {
                            val body = JSONObject(response.body()?.string())
                            val token = Constants.KEY_BEARER+body.getString(Constants.KEY_RES_TOKEN)
                            mListener.onSetPasswordButtonClicked(token)
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                    } else {
                        try {
                            val body = response.errorBody()?.string()
                            confirmPassword.error = body
                        } catch (e: Exception) {
                            Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                        }
                    }
                },{
                    error ->
                    Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnLoginActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnMainActivityInteractionListener")
        }
    }

}
