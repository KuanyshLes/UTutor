package com.support.robigroup.ututor.ui.chat.holders

import android.os.Handler
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.DownloadListener
import com.rx2androidnetworking.Rx2AndroidNetworking
import com.stfalcon.chatkit.messages.MessageHolders
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.R
import com.support.robigroup.ututor.commons.Functions
import com.support.robigroup.ututor.ui.chat.AudioPlayerCallback
import com.support.robigroup.ututor.ui.chat.PlayView
import com.support.robigroup.ututor.ui.chat.model.ChatMessage
import io.realm.Realm


class OutcomingAudioMessageVH(itemView: View) : MessageHolders.OutcomingTextMessageViewHolder<ChatMessage>(itemView) {
    private val mPlayPauseBtn: ImageButton = itemView.findViewById(R.id.btn_play_pause)
    private val seekBar: SeekBar = itemView.findViewById(R.id.progress)
    private val mDownloadProgreesBar: ProgressBar = itemView.findViewById(R.id.download_progress)
    private val playTime: TextView = itemView.findViewById(R.id.play_time)
    private val mListener = (itemView.context as PlayView).getPlayPresenter()
    private val dataManager = mListener.provideDataManager()

    private var handler = Handler()
    private var duration: Int = 0
    private var stepToUpdate: Int = 200
    private var isTrackingSeekbar = false


    private val mUpdateTimeTask = object : Runnable {
        override fun run() {
            val pos = mListener.getPlayerCurrentPosition()
            if(pos!=null && pos<duration){
                playTime.text = Functions.getTimerFromMillis(pos)
                seekBar.progress = pos
                handler.postDelayed(this, stepToUpdate.toLong())
            }

        }
    }

    override fun onBind(message: ChatMessage) {
        super.onBind(message)
        time.setTextColor(time.context.resources.getColor(R.color.colorGrey))

        //outcoming logic different
        if(message.localFilePath==null || !dataManager.checkFileExistance(message.localFilePath)){
            message.playingPosition = 0
            message.status = Constants.MESSAGE_DOWNLOAD
            if(message.localFilePath!=null){
                val r = Realm.getDefaultInstance()
                r.executeTransaction {
                    message.localFilePath = null
                }
                r.close()
            }
        }else{
            if(message.status == Constants.MESSAGE_DOWNLOAD || message.playingPosition == 0)
                message.status = Constants.MESSAGE_STOPPED
        }
        //end outcoming logic

        when(message.status){
            Constants.MESSAGE_PAUSE -> {
                statusPause(message)
            }
            Constants.MESSAGE_PLAYING ->{
                statusPlay(message)
            }
            Constants.MESSAGE_DOWNLOAD ->{
                statusDownload(message)
            }
            Constants.MESSAGE_DOWNLOADING ->{
                statusDownloading(message)
            }
            Constants.MESSAGE_STOPPED ->{
                statusStopped(message)
            }
        }

        seekBar.progress = message.playingPosition
        mDownloadProgreesBar.max = 360

        mPlayPauseBtn.setOnClickListener {
            when(message.status){
                Constants.MESSAGE_STOPPED ->{
                    mListener.stopPrevious()
                    mListener.setPlayerCallback(object : AudioPlayerCallback {
                        override fun onNewPlay() {
                            message.playingPosition = 0
                            statusPause(message)
                        }

                        override fun onComplete() {
                            message.playingPosition = 0
                            statusStopped(message)
                        }

                        override fun onReady(wholeDuration: Int) {
                            duration = wholeDuration
                            seekBar.max = duration
                            statusPlay(message)
                        }
                    })
                    mListener.onPlayClick(message)
                }
                Constants.MESSAGE_PAUSE -> {
                    mListener.resumePlay()
                    statusPlay(message)
                }
                Constants.MESSAGE_PLAYING ->{
                    mListener.onPauseClick()
                    message.playingPosition = mListener.getPlayerCurrentPosition()
                    statusPause(message)
                }
                Constants.MESSAGE_DOWNLOAD ->{
                    startDownload(message)
                }

            }
        }
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if(b){
                    mListener.onSeekChanged(i)
                }else{

                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTrackingSeekbar = true
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isTrackingSeekbar = false
            }
        })
        if (bubble != null) {
            val valueInPixels = bubble.context.resources.getDimension(R.dimen.bubble_padding).toInt()
            bubble.setPadding(valueInPixels,
                    valueInPixels,
                    valueInPixels,
                    valueInPixels)
        }
    }

    private fun statusPlay(message: ChatMessage) {
        seekBar.isEnabled = true
        message.status = Constants.MESSAGE_PLAYING
        mPlayPauseBtn.clearAnimation()
        mPlayPauseBtn.setImageResource(R.drawable.ic_pause_media)
        handler.postDelayed(mUpdateTimeTask, stepToUpdate.toLong())
    }

    private fun statusPause(message: ChatMessage) {
        handler.removeCallbacks(mUpdateTimeTask)
        message.status = Constants.MESSAGE_PAUSE
        mPlayPauseBtn.clearAnimation()
        mPlayPauseBtn.setImageResource(R.drawable.ic_play_media_black)
        seekBar.progress = message.playingPosition
        seekBar.isEnabled = true
    }

    private fun statusStopped(message: ChatMessage) {
        handler.removeCallbacks(mUpdateTimeTask)
        message.status = Constants.MESSAGE_STOPPED
        mPlayPauseBtn.clearAnimation()
        playTime.text = "00:00"
        mPlayPauseBtn.setImageResource(R.drawable.ic_play_media_black)
        message.playingPosition = 0
        seekBar.progress = message.playingPosition
        seekBar.isEnabled = false
    }

    private fun statusDownloading(message: ChatMessage) {
        message.status = Constants.MESSAGE_DOWNLOADING
        mPlayPauseBtn.visibility = View.GONE
        mDownloadProgreesBar.visibility = View.VISIBLE
        seekBar.isEnabled = false
    }

    private fun statusDownload(message: ChatMessage) {
        seekBar.isEnabled = false
        message.status = Constants.MESSAGE_DOWNLOAD
        mPlayPauseBtn.setImageResource(R.drawable.ic_down_arrow)
    }

    private fun startDownload(message: ChatMessage){
        val dirPath = dataManager.getDownloadSaveDir()
        val fileName = dataManager.getDownloadFileName(message.id)
        val url = message.imageUrl
        statusDownloading(message)
        Rx2AndroidNetworking.download(url, dirPath, fileName)
                .setTag(message.id)
                .setPriority(Priority.MEDIUM)
                .doNotCacheResponse()
                .build()
                .setDownloadProgressListener { bytesDownloaded, totalBytes ->
                    val progress = ((bytesDownloaded * 360 / totalBytes)).toInt()
                    mDownloadProgreesBar.progress = progress
                }
                .startDownload(object : DownloadListener {
                    override fun onDownloadComplete() {
                        mPlayPauseBtn.visibility = View.VISIBLE
                        mDownloadProgreesBar.visibility = View.GONE
                        statusStopped(message)
                        val r = Realm.getDefaultInstance()
                        r.executeTransaction {
                            message.localFilePath = dataManager.getDownloadSavePath(message.id)
                        }
                        r.close()
                    }

                    override fun onError(error: ANError) {
                        mPlayPauseBtn.visibility = View.VISIBLE
                        mDownloadProgreesBar.visibility = View.GONE
                        statusDownload(message)
                        dataManager.removeFile(dirPath+fileName)
                    }
                })
    }
}