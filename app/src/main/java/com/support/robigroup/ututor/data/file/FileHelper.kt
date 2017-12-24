package com.support.robigroup.ututor.data.file

/**
 * Created by Bimurat Mukhtar on 17.11.2017.
 */

interface FileHelper{
    fun getSentSavePath(chatId: String): String
    fun getDownloadSavePath(messageId: String): String
    fun getDownloadSaveDir(): String
    fun getDownloadFileName(messageId: String): String
    fun checkMessageFileExistance(messageId: String): Boolean
    fun checkFileExistance(url: String): Boolean
    fun cleanDirectories()
    fun removeFile(path: String)
}
