package com.support.robigroup.ututor.features.profile

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Learner
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener

class ProfileFragment : Fragment() {

    private var learner: Learner? = null
    private var mListener: OnMainActivityInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            learner = Gson().fromJson(arguments.getString(ARG_LEARNER),Learner::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnMainActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    companion object {

        private val ARG_LEARNER = "learner"

        fun newInstance(learner: Learner): ProfileFragment {
            val fragment = ProfileFragment()
            val args = Bundle()
            args.putString(ARG_LEARNER, Gson().toJson(learner,Learner::class.java))
            fragment.arguments = args
            return fragment
        }
    }
}
