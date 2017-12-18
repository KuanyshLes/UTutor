package com.support.robigroup.ututor.utils

import com.support.robigroup.ututor.Constants
import java.io.File


object FileUtils {

    fun getSentSavePath(chatId: String): String {
        val path = Constants.BASE_AUDIO_FOLDER + "sent/"+ chatId + "/"
        val dir = File(path)
        if(!dir.exists())
            dir.mkdirs()
        return path + dir.listFiles().size + "audio.wav"
    }

    fun getDownloadSavePath(filePath: String){

    }
}
