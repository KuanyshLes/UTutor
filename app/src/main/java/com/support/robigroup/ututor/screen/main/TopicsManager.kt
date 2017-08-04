package com.support.robigroup.ututor.screen.main

import com.support.robigroup.ututor.api.RestAPI
import com.support.robigroup.ututor.commons.logd
import com.support.robigroup.ututor.model.content.ClassRoom
import com.support.robigroup.ututor.model.content.Lesson
import com.support.robigroup.ututor.model.content.TopicItem
import io.reactivex.Observable


class TopicsManager(private val api: RestAPI = RestAPI()) {

    companion object {
        private val topics = arrayListOf(
                TopicItem(
                        "Математика",
                        "Mukhtar","Дробный функция Обыкновенная дробь — это частное двух чисел, записанное определенным образом. Частное записывается как делимое (верхняя часть дроби) и делитель (нижняя часть дроби), но вместо знака деления между ",
                        9.5,1368436083157,"myid1",9
                ),
                TopicItem(
                        "physika",
                        "Mukhtar","Дробный функция Обыкновенная дробь — это частное двух чисел, записанное определенным образом. Частное записывается как делимое (верхняя часть дроби) и делитель (нижняя часть дроби), но вместо знака деления между ",
                        9.5,1368436083157,"myid2",9
                ),
                TopicItem(
                        "algebra",
                        "Mukhtar","Дробный функция Обыкновенная дробь — это частное двух чисел, записанное определенным образом. Частное записывается как делимое (верхняя часть дроби) и делитель (нижняя часть дроби), но вместо знака деления между ",
                        9.5,1368436083157,"myid3",9
                ),
                TopicItem(
                        "geometriya",
                        "Mukhtar","Дробный функция Обыкновенная дробь — это частное двух чисел, записанное определенным образом. Частное записывается как делимое (верхняя часть дроби) и делитель (нижняя часть дроби), но вместо знака деления между ",
                        9.5,1368436083157,"myid4",8
                ),
                TopicItem(
                        "predposlednei",
                        "Mukhtar","Дробный функция Обыкновенная дробь — это частное двух чисел, записанное определенным образом. Частное записывается как делимое (верхняя часть дроби) и делитель (нижняя часть дроби), но вместо знака деления между ",
                        9.5,1368436083157,"myid5",5
                ),
                TopicItem(
                        "poslednei",
                        "Mukhtar","Дробный функция Обыкновенная дробь — это частное двух чисел, записанное определенным образом. Частное записывается как делимое (верхняя часть дроби) и делитель (нижняя часть дроби), но вместо знака деления между ",
                        9.5,1368436083157,"myid6",4
                )
                )

        var lessons: Lesson = Lesson("","","", topics)

    }

    fun getLessons(): Observable<ClassRoom>{
        return Observable.create{
            subscriber ->
            try{
                var cla: ClassRoom = ClassRoom(MutableList(20 ,{Lesson("MathPathPhysAlgebra")}))
                Thread.sleep(3000)
                logd("size ".plus(cla.lessons.size))
                subscriber.onNext(cla)
                subscriber.onComplete()
            }catch (e: InterruptedException){
                subscriber.onError(Throwable(e.toString()))
            }
        }
    }
    fun getTopics(after: String, limit: String = "10"): Observable<Lesson> {
        val answer: Observable<Lesson> = Observable.create {
            subscriber ->
            try {
                Thread.sleep(6000)
                val redditNews = Lesson(
                        "",
                        "",
                        "",
                        lessons.news)

                subscriber.onNext(redditNews)
                subscriber.onComplete()
            }catch (e: InterruptedException){
                subscriber.onError(Throwable(e.toString()))
            }
        }

        return answer

//        return Observable.create {
//            subscriber ->
//            val callResponse = api.getTopics(after, limit)
//            val response = callResponse.execute()
//
//            if (response.isSuccessful) {
//                val dataResponse = response.body().data
//                val news = dataResponse.children.map {
//                    val item = it.data
//                    TopicItem(item.author, item.title, item.num_comments,
//                            item.created, item.thumbnail, item.url)
//                }
//                val redditNews = RedditNews(
//                        dataResponse.after ?: "",
//                        dataResponse.before ?: "",
//                        news)
//
//                subscriber.onNext(redditNews)
//                subscriber.onComplete()
//            } else {
//                subscriber.onError(Throwable(response.message()))
//            }
//        }
    }
}