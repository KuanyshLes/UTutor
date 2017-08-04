package com.support.robigroup.ututor.screen.login

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.OnLoginActivityInteractionListener
import com.support.robigroup.ututor.commons.logd
import kotlinx.android.synthetic.main.fragment_reg_2.*


class RegFragment2 : Fragment() {


    private var mListener: OnLoginActivityInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logd("onCreate Reg2")

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        logd("onCreateView Reg2")
        return inflater!!.inflate(R.layout.fragment_reg_2, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logd("onActivityCreated Reg2")
        done_button.setOnClickListener {
            mListener!!.OnDoneButtonClicked(firstName.text.toString(),lastName.text.toString())
        }
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        logd("onAttach Reg2")

        if (context is OnLoginActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnMainActivityInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        logd("onDetach Reg2")
        mListener = null
    }

}
