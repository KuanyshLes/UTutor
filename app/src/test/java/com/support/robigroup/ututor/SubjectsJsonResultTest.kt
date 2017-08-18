package com.support.robigroup.ututor

import org.junit.Assert
import org.junit.Test
import com.google.gson.GsonBuilder
import com.support.robigroup.ututor.commons.ChatLesson
import com.support.robigroup.ututor.model.content.Subject
import com.support.robigroup.ututor.model.content.TopicItem


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
        val results: List<Subject> = gson.fromJson(json , Array<Subject>::class.java).toList()
        Assert.assertNotNull(results)
        Assert.assertNotNull(results[0].Id)
        Assert.assertNotNull(results[0].Text)
        System.out.println(results.toString())
    }

    @Test
    fun isChatLessonCorrect() {
        val gson = GsonBuilder().create()
        val json: String = "{\n" +
                "  \"Id\": 0,\n" +
                "  \"TopicId\": 0,\n" +
                "  \"CreateTime\": \"2017-08-16T10:44:09.436Z\",\n" +
                "  \"StartTime\": \"2017-08-16T10:44:09.436Z\",\n" +
                "  \"EndTime\": \"2017-08-16T10:44:09.436Z\",\n" +
                "  \"StatusId\": 0,\n" +
                "  \"Duration\": \"string\",\n" +
                "  \"TeacherId\": \"string\",\n" +
                "  \"LearnerId\": \"string\",\n" +
                "  \"SubjectName\": \"string\",\n" +
                "  \"TopicTitle\": \"string\",\n" +
                "  \"Class\": 0,\n" +
                "  \"Learner\": \"string\",\n" +
                "  \"Teacher\": \"string\",\n" +
                "  \"TeacherReady\": true,\n" +
                "  \"LearnerReady\": true\n" +
                "}"
        val results = gson.fromJson(json , ChatLesson::class.java)
        Assert.assertNotNull(results)

//        Assert.assertSame(gson.toJson(json,String::class.java),gson.toJson(results,ChatLesson::class.java))
        System.out.println(gson.toJson(results,ChatLesson::class.java))
    }


}