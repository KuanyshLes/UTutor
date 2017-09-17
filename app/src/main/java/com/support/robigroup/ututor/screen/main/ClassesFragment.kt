package com.support.robigroup.ututor.screen.main

import android.content.Context
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.api.MainManager
import com.support.robigroup.ututor.commons.OnMainActivityInteractionListener
import com.support.robigroup.ututor.commons.RxBaseFragment
import com.support.robigroup.ututor.commons.requestErrorHandler
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.screen.main.adapters.ClassAdapter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates


class ClassesFragment : RxBaseFragment() {

    private var mListener: OnMainActivityInteractionListener? = null
    private var mSubject: Subject by Delegates.notNull()
    private var adapter: ClassAdapter by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (arguments != null) {
            mSubject = arguments.getParcelable(ARG_SUBJECT)
        }else if(savedInstanceState!=null){
            mSubject = savedInstanceState.getParcelable(ARG_SUBJECT)
        }
        adapter = ClassAdapter(mListener,mSubject)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_class_list, container, false)

        // Set the adapter
        if (view is RecyclerView) {
            val context = view.getContext()
            view.layoutManager = LinearLayoutManager(context)
            view.adapter = adapter
        }
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mListener!!.setDisplayHomeAsEnabled(true)
        mListener!!.setToolbarTitle(mSubject.Text)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnMainActivityInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnListFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }



    companion object {

        private val ARG_SUBJECT = "subject"

        fun newInstance(subject: Subject): ClassesFragment {
            val fragment = ClassesFragment()
            val args = Bundle()
            args.putParcelable(ARG_SUBJECT, subject)
            fragment.arguments = args
            return fragment
        }
    }
}
