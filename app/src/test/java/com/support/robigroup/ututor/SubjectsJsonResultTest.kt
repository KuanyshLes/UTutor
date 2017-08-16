package com.support.robigroup.ututor

import org.junit.Assert
import org.junit.Test
import com.google.gson.GsonBuilder
import com.support.robigroup.ututor.model.content.Lesson
import com.support.robigroup.ututor.model.content.TopicItem


/**
 * Created by Bimurat Mukhtar on 15.08.2017.
 */
class SubjectsJsonResultTest {
    @Test
    fun isJsonCorrectTopicConvert() {
        val gson = GsonBuilder().create()
        val json: String = "[{\"Id\":2,\"Text\":\"Математика\"},{\"Id\":5,\"Text\":\"Физика\"}]"
        val results: List<TopicItem> = gson.fromJson(json , Array<TopicItem>::class.java).toList()
        Assert.assertNotNull(results)
        Assert.assertNotNull(results[0].Id)
        Assert.assertNotNull(results[0].Text)
        System.out.println(results.toString())
    }

    @Test
    fun isJsonCorrectLessonConvert() {
        val gson = GsonBuilder().create()
        val json: String = "[{\"Id\":2,\"Text\":\"Математика\"},{\"Id\":5,\"Text\":\"Физика\"}]"
        val results: List<Lesson> = gson.fromJson(json , Array<Lesson>::class.java).toList()
        Assert.assertNotNull(results)
        Assert.assertNotNull(results[0].Id)
        Assert.assertNotNull(results[0].Text)
        System.out.println(results.toString())
    }

}