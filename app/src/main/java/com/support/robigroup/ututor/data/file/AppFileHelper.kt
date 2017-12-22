package com.support.robigroup.ututor.data.file

import android.content.Context
import android.util.Log
import com.support.robigroup.ututor.di.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class AppFileHelper @Inject
constructor(@param:ApplicationContext private val mContext: Context) : FileHelper{

    override fun getSentSavePath(chatId: String): String {
        val absol = mContext.filesDir.absolutePath
        val path = absol + "/recorded/"+ chatId + "/"
        val dir = File(path)
        if(!dir.exists())
            dir.mkdirs()
        var max = 0
        for (file in dir.listFiles()){
            if(file.isFile){
                try {
                    val fileName = file.name.toInt()
                    if(fileName>max){
                        max =fileName
                    }
                } catch (e: Exception) {
                    Log.e("File", "error converting to number")
                }
            }
        }
        return path + max + 1 + ".wav"
    }

    override fun getDownloadSavePath(messageId: String): String {
        val absol = mContext.filesDir.absolutePath
        val path = absol + "/downloaded/"
        val dir = File(path)
        if(!dir.exists())
            dir.mkdirs()
        return path + messageId + ".wav"
    }

    override fun checkFileExistance(messageId: String): Boolean {
        val file = File(getDownloadSavePath(messageId))
        return file.exists() && file.isFile
    }
}