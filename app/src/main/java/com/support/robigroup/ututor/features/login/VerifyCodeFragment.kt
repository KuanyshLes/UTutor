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
import kotlinx.android.synthetic.main.fragment_verify_code.*
import org.json.JSONObject

class VerifyCodeFragment : Fragment() {

    lateinit var mListener: OnLoginActivityInteractionListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_code, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        button_verify.setOnClickListener {
            validateCode()
        }
    }

    private fun validateCode(){
        val codeString: String = code.text.toString()
        if(codeString.isEmpty()){
            code.error = getString(R.string.error_field_required)
        }else{
            RestAPI.getApi().postPhone(codeString, mListener.getPhoneNumber(), mListener.getFirstToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        response ->
                        if (response.isSuccessful) {
                            try {
                                val body = JSONObject(response.body()?.string())
                                val token = Constants.KEY_BEARER+body.getString(Constants.KEY_RES_TOKEN)
                                mListener.onVerifyCodeButtonClicked(token)
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        } else {
                            try {
                                val body = response.errorBody()?.string()
                                code.error = body
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    },{
                        error ->
                        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                    })


        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnLoginActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnMainActivityInteractionListener")
        }
    }

}// Required empty public constructor
