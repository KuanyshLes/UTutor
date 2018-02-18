package com.support.robigroup.ututor.utils.commonUtils

import com.support.robigroup.ututor.utils.CommonUtils
import junit.framework.Assert.assertEquals
import org.junit.Test


class CommonUtilsTests {
    @Test
    fun passwordCheck() {
        val exsFalse = arrayOf("1qw2e", "asdfdlkj", "1asdf fwew", "12345678", "asd asd1")
        val exsTrue = arrayOf("?2@3asdf", "asdfdlk1j", "1asdf123fwew", "a12345678", "asd@#$%/asd1")

        for(ex in exsFalse){
            assertEquals(false, CommonUtils.isPasswordValid(ex))
        }
        for(ex in exsTrue){
            assertEquals(true, CommonUtils.isPasswordValid(ex))
        }
    }
}