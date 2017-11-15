package com.support.robigroup.ututor.ui.chat.eval

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.ChatInformation
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.di.component.ActivityComponent
import com.support.robigroup.ututor.ui.base.BaseDialog
import com.support.robigroup.ututor.ui.chat.ActivityChat
import com.support.robigroup.ututor.ui.chat.RateMvpView
import javax.inject.Inject

class RateDialog : BaseDialog(), RateMvpView {

    @Inject
    lateinit var ratePresenter: RatePresenter<RateMvpView>
    lateinit var textSum: TextView
    lateinit var textDuration: TextView
    var ratingBar: RatingBar? = null


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val component = activityComponent
        if (component != null) {
            component.inject(this)
            ratePresenter.onAttach(this)
        }

        val builder = AlertDialog.Builder(activity)
        val view = activity.layoutInflater.inflate(R.layout.dialog_finish,null)
        builder.setView(view)
        return builder.create()
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        setUp(view!!)
        ratePresenter.onViewInitialized()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun initViews(info: ChatInformation) {
        textSum.text = String.format("%s₸", info.InvoiceSum)
        try {
            textDuration.text = String.format("%s %s",
                    getString(R.string.duration_short),
                    Functions.getTimeWaiting(info.Duration?.toLong()!!))
        }catch (e: Exception){
            textDuration.text = Functions.getTimeWaiting(1000)
        }
    }

    override fun setUp(view: View) {
        val buttonEvaluate = view.findViewById<Button>(R.id.button_evaluate) as Button
        buttonEvaluate.setOnClickListener {
            ratePresenter.onClickRateButton(ratingBar!!.rating)
            dismiss()
        }
        textSum = view.findViewById<TextView>(R.id.sum_text) as TextView
        textDuration = view.findViewById<TextView>(R.id.duration_text) as TextView
        ratingBar = view.findViewById<RatingBar>(R.id.rating_bar) as RatingBar
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        (activity as ActivityChat).startMenuActivity()
    }

}