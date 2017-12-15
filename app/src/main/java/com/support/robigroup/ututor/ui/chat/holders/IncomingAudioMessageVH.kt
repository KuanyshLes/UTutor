package com.support.robigroup.ututor.ui.chat.holders

import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import com.google.gson.Gson

import com.stfalcon.chatkit.messages.MessageHolders
import com.stfalcon.chatkit.utils.DateFormatter
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.features.chat.model.ChatMessage
import com.support.robigroup.ututor.ui.chat.PlayPresenter
import com.support.robigroup.ututor.ui.chat.AudioPlayerCallback
import com.support.robigroup.ututor.ui.chat.PlayView


class IncomingAudioMessageVH(itemView: View): MessageHolders.IncomingTextMessageViewHolder<ChatMessage>(itemView) {

    private var mPlayPauseBtn: ImageButton = itemView.findViewById(R.id.btn_play_pause)
    private var seekBar: SeekBar = itemView.findViewById(R.id.progress)
    private var play_time: TextView = itemView.findViewById(R.id.play_time)
    private var mListener: PlayPresenter = (itemView.context as PlayView).getPlayPresenter()

    private var handler = Handler()
    private var duration: Int = 0
    private var stepToUpdate: Int = 0
    private var progress: Int = 0

    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            if (stepToUpdate * seekBar.progress < duration) {
                progress += 1
                seekBar.progress = progress
                play_time.text = Functions.getTimerFromMillis(mListener.getPlayerCurrentPosition())
            }
            handler.postDelayed(this, stepToUpdate.toLong())

        }
    }

    override fun onBind(message: ChatMessage) {
        super.onBind(message)

        Log.w("Message", Gson().toJson(message, ChatMessage::class.java))

        time.text = DateFormatter.format(message.createdAt, DateFormatter.Template.TIME)

        seekBar.progress = 0
        seekBar.max = 100

        mPlayPauseBtn.tag = Constants.TAG_AUDIO_PAUSE
        mPlayPauseBtn.setOnClickListener {
            if (mPlayPauseBtn.tag.toString() == Constants.TAG_AUDIO_PAUSE) {
                mListener.stopPrevious()
                download()
                Log.e("Audio", "onPlayClicked")
                mListener.setPlayerCallback(object : AudioPlayerCallback {
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
                mListener.onPlayClick(message)

            } else if (mPlayPauseBtn.tag.toString() == Constants.TAG_AUDIO_PLAY) {
                Log.e("Audio", "onPlaySoStop")
                mListener.onPauseClick()
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
