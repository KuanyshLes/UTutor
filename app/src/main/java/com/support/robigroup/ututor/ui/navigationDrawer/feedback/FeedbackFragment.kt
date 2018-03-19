package com.support.robigroup.ututor.ui.navigationDrawer.feedback

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.inflate
import com.support.robigroup.ututor.ui.base.BaseFragment
import com.support.robigroup.ututor.ui.navigationDrawer.DrawerMvpView
import kotlinx.android.synthetic.main.fragment_feedback.*
import javax.inject.Inject

class FeedbackFragment : BaseFragment(), FeedbackFragmentMvpView {

    @Inject
    lateinit var mPresenter: FeedbackFragmentMvpPresenter<FeedbackFragmentMvpView>

    private var mListener: DrawerMvpView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = container?.inflate(R.layout.fragment_feedback)
        activityComponent.inject(this)
        mPresenter.onAttach(this)
        return view
    }

    override fun setUp(view: View?) {
        mListener?.setActionBarTitle(getString(R.string.drawer_item_feedback))
        sendFeedbackButton.setOnClickListener {
            mPresenter.onClickSend(sendFeedbackButton.text.toString())
        }
    }

    override fun onFeedbackSend() {
        Toast.makeText(baseActivity, getString(R.string.feedback_send), Toast.LENGTH_LONG).show()
    }

    override fun setDescriptionError(error: String?) {
        if (error != null) {
            description.error = null
        } else if (error == "") {
            description.error = getString(R.string.error_field_required)
        } else {
            description.error = error
        }
    }

    override fun getVersionCode(): String {
        return baseActivity.packageManager.getPackageInfo(baseActivity.packageName, 0).versionCode.toString()
    }

    override fun getVersionName(): String {
        return baseActivity.packageManager.getPackageInfo(baseActivity.packageName, 0).versionName
    }

    override fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            capitalize(model)
        } else capitalize(manufacturer) + " " + model
    }

    private fun capitalize(str: String): String {
        if (TextUtils.isEmpty(str)) {
            return str
        }
        val arr = str.toCharArray()
        var capitalizeNext = true

        val phrase = StringBuilder()
        for (c in arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c))
                capitalizeNext = false
                continue
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true
            }
            phrase.append(c)
        }

        return phrase.toString()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
        mPresenter.onDetach()
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is DrawerMvpView) {
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
        fun newInstance(): FeedbackFragment {
            return FeedbackFragment()
        }

        const val TAG = "FeedbackFragment"
    }
}
