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
import com.support.robigroup.ututor.commons.logd
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
        val view = activity!!.layoutInflater.inflate(R.layout.dialog_finish,null)
        builder.setView(view)
        setUp(view!!)
        ratePresenter.onViewInitialized()
        return builder.create()
    }

    override fun initViews(info: ChatInformation) {
        textSum.text = String.format("%s₸, %s %s%s", info.InvoiceSum, getString(R.string.tarif), info.InvoiceTariff, getString(R.string.price_ratio))
        try {
            textDuration.text = String.format("%s %s",
                    getString(R.string.duration_short),
                    Functions.getTimeWaiting(info.Duration?.toLong()!!))
        }catch (e: Exception){
            textDuration.text = Functions.getTimeWaiting(1000)
        }
    }

    override fun setUp(view: View) {
        val buttonEvaluate = view.findViewById(R.id.button_evaluate) as Button
        buttonEvaluate.setOnClickListener {
            logd("rating "+ratingBar!!.rating.toString())
            ratePresenter.onClickRateButton(ratingBar!!.rating)
        }
        textSum = view.findViewById(R.id.sum_text) as TextView
        textDuration = view.findViewById(R.id.duration_text) as TextView
        ratingBar = view.findViewById(R.id.rating_bar) as RatingBar
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        (activity as ActivityChat).startMenuActivity()

    }

}