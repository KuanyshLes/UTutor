package com.support.robigroup.ututor.data.file

import android.content.Context
import android.util.Log
import com.support.robigroup.ututor.di.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import android.os.Environment.getExternalStorageDirectory




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
                    val fileName = file.name
                    val fileNumber = fileName.substring(0, fileName.indexOf(".")).toInt()
                    if(fileNumber>max){
                        max =fileNumber
                    }
                } catch (e: Exception) {
                    Log.e("File", "error converting to number")
                }
            }
        }
        return path + (max + 1) + ".wav"
    }

    override fun getDownloadSavePath(messageId: String): String {
        val absol = mContext.filesDir.absolutePath
        val path = getDownloadSaveDir()
        return path + getDownloadFileName(messageId)
    }

    override fun getDownloadSaveDir(): String {
        val absol = mContext.filesDir.absolutePath
        val path = absol + "/downloaded/"
        val dir = File(path)
        if(!dir.exists())
            dir.mkdirs()
        return path
    }

    override fun getDownloadFileName(messageId: String): String {
        return messageId + ".wav"
    }

    override fun checkFileExistance(url: String): Boolean {
        val file = File(url)
        return file.exists()&&file.isFile
    }

    override fun checkMessageFileExistance(messageId: String): Boolean {
        val file = File(getDownloadSavePath(messageId))
        return file.exists() && file.isFile
    }

    override fun cleanDirectories() {
        val absol = mContext.filesDir.absolutePath
        val dir = File(absol)
        if (dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                File(dir, children[i]).delete()
            }
        }
    }

    override fun removeFile(path: String) {
        val dir = File(path)
        if (dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                File(dir, children[i]).delete()
            }
        }
    }
}