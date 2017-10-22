package com.support.robigroup.ututor.features.login


import android.content.Context
import android.os.Bundle
import android.support.annotation.MainThread
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.OnLoginActivityInteractionListener
import com.support.robigroup.ututor.commons.requestErrorHandler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_registration.*
import android.widget.Toast
import com.support.robigroup.ututor.Constants
import retrofit2.adapter.rxjava2.Result.response
import org.json.JSONObject
import org.json.JSONException






/**
 * A simple [Fragment] subclass.
 */
class RegistrationFragment : Fragment() {

    private var mListener: OnLoginActivityInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        next_button.setOnClickListener {
            validateInputs()
        }
    }

    private fun validateInputs(){

        reg_surname.error = null
        reg_name.error = null
        reg_email.error = null

        val surname = reg_surname.text.toString()
        val name = reg_name.text.toString()
        val email = reg_email.text.toString()
        var cancel = false

        if(surname.isEmpty()){
            reg_surname.error = getString(R.string.error_field_required)
            cancel = true
        }
        if(name.isEmpty()){
            reg_name.error = getString(R.string.error_field_required)
            cancel = true
        }
        if(email.isEmpty()){
            reg_email.error = getString(R.string.error_field_required)
            cancel = true
        }else if(!isEmailValid(email)){
            reg_email.error = getString(R.string.error_invalid_email)
            cancel = true
        }

        if(!cancel){
            RestAPI.getApi().register(name,surname,email)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe ({
                        response ->
                        if (response.isSuccessful) {
                            try {
                                val body = JSONObject(response.body()?.string())
                                val token = Constants.KEY_BEARER+body.getString(Constants.KEY_RES_TOKEN)
                                mListener?.onNextButtonClicked(token)
                            } catch (e: Exception) {
                                Toast.makeText(context, e.message, Toast.LENGTH_LONG).show()
                            }

                        } else {
                            try {
                                val body = JSONObject(response.errorBody()?.string())
                                val keys = body.keys()
                                while (keys.hasNext()) {
                                    val key = keys.next() as String
                                    val value = body.getString(key)
                                    when(key){
                                        "Email" -> reg_email.error = value
                                        "" -> Snackbar.make(
                                                activity.findViewById(android.R.id.content),
                                                value,
                                                Snackbar.LENGTH_SHORT
                                        ).show()
                                        "FirstName" -> reg_name.error = value
                                        "LastName" -> reg_name.error = value
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
        }

    }

    @Throws(JSONException::class)
    fun jsonToMap(t: String) {
        val map = HashMap<String, String>()
        val jObject = JSONObject(t)
        val keys = jObject.keys()
        while (keys.hasNext()) {
            val key = keys.next() as String
            val value = jObject.getString(key)
            map.put(key, value)
        }
    }

    private fun isEmailValid(email: String): Boolean {
        return email.contains("@")
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnLoginActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnMainActivityInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

}
