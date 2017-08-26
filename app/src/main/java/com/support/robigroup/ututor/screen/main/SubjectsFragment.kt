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
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlin.properties.Delegates


class SubjectsFragment : RxBaseFragment() {

    private var mListener: OnMainActivityInteractionListener? = null
    private var classNumber: Int by Delegates.notNull()
    private var language: String by Delegates.notNull()
    private var adapter: MySubjectRecyclerViewAdapter by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        if (arguments != null) {
            classNumber = arguments.getInt(ARG_CLASS_NUMBER)
            language = arguments.getString(ARG_LANGUAGE)
        }
        adapter = MySubjectRecyclerViewAdapter(ArrayList(), mListener)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_subject_list, container, false)

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
        requestSubjects(classNumber,language)

        mListener!!.setDisplayHomeAsEnabled(true)
        mListener!!.setToolbarTitle("$classNumber ${getString(R.string.class_name)}")
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

    private fun requestSubjects(classNumber :Int, lang: String){
        val subscription = MainManager().getLessons(classNumber,lang)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { retrievedLessons ->
                            if(activity.requestErrorHandler(retrievedLessons.code(),retrievedLessons.message())){
                                adapter.clearAndAddSubjects(retrievedLessons.body()?.map {
                                    it.classNumber = classNumber
                                    it
                                })
                            }else{
                                //TODO handle http errors
                            }
                        },
                        { e ->
                            Snackbar.make(view!!, e.message ?: "", Snackbar.LENGTH_LONG).show()
                        }
                )
        subscriptions.add(subscription)
    }

    companion object {

        // TODO: Customize parameter argument names
        private val ARG_CLASS_NUMBER = "classNumber"
        private val ARG_LANGUAGE = "language"

        // TODO: Customize parameter initialization
        fun newInstance(classNumber: Int, lang: String): SubjectsFragment {
            val fragment = SubjectsFragment()
            val args = Bundle()
            args.putInt(ARG_CLASS_NUMBER, classNumber)
            args.putString(ARG_LANGUAGE, lang)
            fragment.arguments = args
            return fragment
        }
    }
}
