package com.support.robigroup.ututor.ui.chat

import android.os.Bundle
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.features.main.MenuActivity
import com.support.robigroup.ututor.ui.base.BaseActivity
import com.support.robigroup.ututor.ui.chat.ready.ReadyDialog
import kotlinx.android.synthetic.main.activity_chat.*
import javax.inject.Inject

class ActivityChat : BaseActivity(), ChatMvpView {



    @Inject
    lateinit var mPresenter: ChatMvpPresenter<ChatMvpView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        activityComponent.inject(this)
        mPresenter.onAttach(this)

        setUp()
    }

    override fun setUp() {
        setSupportActionBar(toolbar)
        text_finish.setOnClickListener { mPresenter.onFinishClick() }
        mPresenter.onViewInitialized()
    }

    override fun openMenuActivity() {
        MenuActivity.open(this)
        finish()
    }

    override fun showFinishDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeFinishDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeReadyDialog() {
        val dialog = supportFragmentManager.findFragmentByTag(Constants.TAG_READY_DIALOG) as ReadyDialog?
        dialog?.dismiss()
    }

    override fun showReadyDialog(dif: Long) {
        val dialog = ReadyDialog()
        dialog.isCancelable = false
        dialog.startShow(supportFragmentManager, Constants.TAG_READY_DIALOG, dif)
    }

    override fun showEvalDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun closeEvalDialog() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onLearnerReadyDialog() {
        val dialog = supportFragmentManager.findFragmentByTag(Constants.TAG_READY_DIALOG) as ReadyDialog?
        dialog?.updateButtonText()
    }

    override fun changeCounterValueText(text: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        mPresenter.onDetach()
        super.onDestroy()
    }
}
