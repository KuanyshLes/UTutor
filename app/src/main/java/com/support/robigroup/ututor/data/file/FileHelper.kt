package com.support.robigroup.ututor.data.file

/**
 * Created by Bimurat Mukhtar on 17.11.2017.
 */

interface FileHelper{
    fun getSentSavePath(chatId: String): String
    fun getDownloadSavePath(messageId: String): String
    fun checkFileExistance(messageId: String): Boolean
}
