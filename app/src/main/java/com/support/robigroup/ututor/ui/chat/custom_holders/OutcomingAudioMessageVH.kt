package com.support.robigroup.ututor.ui.chat.custom_holders

import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView

import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.utils.DateFormatter
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.chat.AudioHolderPresenter
import com.support.robigroup.ututor.ui.chat.AudioPlayerCallback
import com.support.robigroup.ututor.ui.chat.ChatMvpView

/**
 * Created by Bimurat Mukhtar on 01.12.2017.
 */

class OutcomingAudioMessageVH(itemView: View) : MessageHolders.OutcomingTextMessageViewHolder<ChatMessage>(itemView) {

    lateinit var mPlayPauseBtn: ImageButton
    lateinit var seekBar: SeekBar
    lateinit var play_time: TextView
    lateinit var mPresenter: AudioHolderPresenter


    private var handler = Handler()


    private var duration: Int = 0
    private var stepToUpdate: Int = 0
    private var progress: Int = 0


    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            if (stepToUpdate * seekBar.progress < duration) {
                progress += 1
                seekBar.progress = progress
                play_time.text = Functions.getTimerFromMillis(mPresenter.getPlayerCurrentPosition())
            }
            handler.postDelayed(this, stepToUpdate.toLong())

        }
    }

    init {
        mPresenter = (itemView.context as ChatMvpView).getAudioPresenter()
        mPlayPauseBtn = itemView.findViewById(R.id.btn_play_pause)
        seekBar = itemView.findViewById(R.id.progress)
        play_time = itemView.findViewById(R.id.play_time)
    }

    override fun onBind(message: ChatMessage) {
        super.onBind(message)

        time.text = DateFormatter.format(message.createdAt, DateFormatter.Template.TIME)

        seekBar.progress = 0
        seekBar.max = 100

        mPlayPauseBtn.setOnClickListener {
            if (mPlayPauseBtn.tag.toString() == Constants.TAG_AUDIO_PAUSE) {
                mPresenter.stopPrevious()
                download()
                Log.e("Audio", "onStopSoPlay")
                mPresenter.setPlayerCallback(object : AudioPlayerCallback {
                    override fun onNewPlay() {
                        Log.e("Audio", "onNewPlay")
                        stop()
                    }

                    override fun onProgressChanged(cDur: Long, tDur: Long) {
                        play_time.text = Functions.getTimerFromMillis(tDur)

                        // Updating progress bar
                        val progress = Functions.getProgressPercentage(tDur, cDur)
                        Log.e("Audio", "progress " + progress)
                        seekBar.progress = progress
                    }

                    override fun onComplete() {
                        stop()
                    }

                    override fun onReady(wholeDuration: Int) {
                        progress = 0
                        duration = wholeDuration
                        stepToUpdate = duration / 100
                        play()
                    }

                })
                mPresenter.onPlayClick(message.imageUrl)

            } else if (mPlayPauseBtn.tag.toString() == Constants.TAG_AUDIO_PLAY) {
                Log.e("Audio", "onPlaySoStop")
                mPresenter.onPauseClick()
                stop()
            }
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {

            }
        })
    }

    private fun play() {
        mPlayPauseBtn.clearAnimation()
        mPlayPauseBtn.setImageResource(R.drawable.ic_stop_media_black)
        mPlayPauseBtn.tag = Constants.TAG_AUDIO_PLAY
        handler.postDelayed(mUpdateTimeTask, stepToUpdate.toLong())
    }

    private fun stop() {
        mPlayPauseBtn.clearAnimation()
        play_time.text = ""
        mPlayPauseBtn.setImageResource(R.drawable.ic_play_media_black)
        seekBar.progress = 0
        mPlayPauseBtn.tag = Constants.TAG_AUDIO_PAUSE
        handler.removeCallbacks(mUpdateTimeTask)

    }

    private fun download() {
        mPlayPauseBtn.tag = Constants.TAG_AUDIO_DOWNLOAD
        mPlayPauseBtn.setImageResource(R.drawable.ic_spinner_of_dots)
        mPlayPauseBtn.startAnimation(AnimationUtils.loadAnimation(mPlayPauseBtn.context, R.anim.download_rotate))
    }
}