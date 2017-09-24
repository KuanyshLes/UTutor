package com.support.robigroup.ututor.screen.teachers

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson

import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Lesson
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import kotlin.properties.Delegates

class LastTopicFragment : Fragment() {

    private var lesson: Lesson by Delegates.notNull()

    private var mListener: OnMainActivityInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            lesson = Gson().fromJson(arguments.getString(ARG_LESSON),Lesson::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_last_topic, container, false)
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

        private val ARG_LESSON = "lesson"

        fun newInstance(lesson: Lesson): LastTopicFragment {
            val fragment = LastTopicFragment()
            val args = Bundle()
            args.putString(ARG_LESSON, Gson().toJson(lesson,Lesson::class.java))
            fragment.arguments = args
            return fragment
        }
    }
}// Required empty public constructor
