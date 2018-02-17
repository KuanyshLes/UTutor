package com.support.robigroup.ututor.features.login

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.OnLoginActivityInteractionListener
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_reg_2.*
import org.json.JSONObject


class GetCodeFragment : Fragment() {

    lateinit var mListener: OnLoginActivityInteractionListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_reg_2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        sendPhoneNumberButton.setOnClickListener {
            validatePhone()
        }
    }

    private fun validatePhone(){
        val phoneNumber = phone.text.toString()
        if(android.util.Patterns.PHONE.matcher(phoneNumber).matches()){
            RestAPI.getApi().getPhone(phoneNumber, mListener.getFirstToken())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        response ->
                        if (response.isSuccessful) {
                            mListener.onGetCodeButtonClicked(phoneNumber)
                        } else {
                            try {
                                val body = JSONObject(response.errorBody()?.string())
                                val keys = body.keys()
                                while (keys.hasNext()) {
                                    val key = keys.next() as String
                                    val value = body.getString(key)
                                    when(key){
                                        "PhoneNumber" -> phone.error = value
                                        "" -> Snackbar.make(
                                                activity!!.findViewById(android.R.id.content),
                                                value,
                                                Snackbar.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }
                        }
                    },{
                        error ->
                        Toast.makeText(context, error.message, Toast.LENGTH_LONG).show()
                    })
        }else{
            phone.error = getString(R.string.error_phone_number)
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

}
