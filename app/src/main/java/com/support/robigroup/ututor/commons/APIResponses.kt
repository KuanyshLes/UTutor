package com.support.robigroup.ututor.commons

class RedditNewsResponse(val data: RedditDataResponse)

class RedditDataResponse(
        val children: List<RedditChildrenResponse>,
        val after: String?,
        val before: String?
)

class RedditChildrenResponse(val data: RedditNewsDataResponse)

class RedditNewsDataResponse(
        val author: String,
        val title: String,
        val num_comments: Int,
        val created: Long,
        val thumbnail: String,
        val url: String
)



class ChatLesson(
        val Id: Int,
        val TopicId: Int,
        val CreateTime: String,
        val StartTime: String,
        val EndTime: String,
        val StatusId: Int,
        val Duration: String,
        val TeacherId: String,
        val LearnerId: String,
        val SubjectName: String,
        val TopicTitle: String,
        val Class: Int,
        val Learner: String,
        val Teacher: String,
        val TeacherReady: Boolean,
        val LearnerReady: Boolean
)

class MessagesResponse(
        val Id: Int,
        val UserId: String,
        val LessonId: Int,
        val Time: String,
        val Text: String,
        val Lesson: Lesson
)
class Lesson(
        val Id: Int,
        val TopicId: Int,
        val CreateTime: String,
        val StartTime: String,
        val EndTime: String,
        val StatusId: Int,
        val Duration: String,
        val TeacherId: String,
        val LearnerId: String,
        val TeacherReady: Boolean,
        val LearnerReady: Boolean,
        val LessonMessages: List<LessonMessage>,
        val LessonStatus: LessonStatus,
        val Teacher: Teacher,
        val Topic: Topic
)

class LessonMessage (
        val Id: Int,
        val UserId: String,
        val LessonId: Int,
        val Time: String,
        val Text: String,
        val Lesson: Lesson
)

class LessonStatus(
        val Id: Int,
        val Name: String,
        val Description: String,
        val Lessons: List<Lesson>
)

class Teacher(
        val Id: String,
        val Raiting: Float,
        val Languages: String,
        val Classes: String,
        val AspNetUser: AspNetUser,
        val Lessons: List<Lesson>,
        val TeacherTopics: List<TeacherTopic>,
        val LessonRequests: List<LessonRequest>
)

class Topic(
        val Id: Int,
        val Title: String,
        val Description: String,
        val SubjectId: Int,
        val Lessons: List<Lesson>,
        val Subject: Subject,
        val TeacherTopics: List<TeacherTopic>,
        val LessonRequests: List<LessonRequest>
)

class AspNetUser(
        val Id: String,
        val AccessFailedCount: Int,
        val ConcurrencyStamp: String,
        val Email: String,
        val EmailConfirmed: Boolean,
        val LockoutEnabled: Boolean,
        val LockoutEnd: String,
        val NormalizedEmail : String,
        val NormalizedUserName : String,
        val PasswordHash : String,
        val PhoneNumber : String,
        val PhoneNumberConfirmed : Boolean,
        val SecurityStamp : String,
        val TwoFactorEnabled : Boolean,
        val UserName : String,
        val FirstName : String,
        val LastName : String,
        val MiddleName : String,
        val Birthday : String,
        val Learner  : Learner,
        val Teacher: Teacher
)

class TeacherTopic(
        val Id: Int,
        val TeacherId: String,
        val TopicId: Int,
        val Teacher: Teacher,
        val Topic: Topic
)

class LessonRequest(
        val Id: String,
        val LearnerId: String,
        val TeacherId: String,
        val TopicId: Int,
        val RequestTime: String,
        val Teacher: Teacher,
        val Topic: Topic
)

class Subject(
        val Id: Int,
        val Name: String,
        val Class: Int,
        val LanguageCode: String,
        val Description: String,
        val TeacherSubjects: List<TeacherSubject>,
        val Topics: List<Topic>
)

class Learner(
        val Id: String,
        val AspNetUser: AspNetUser
)

class TeacherSubject(
        val TeacherId: String,
        val SubjectId: Int,
        val Raiting: Float,
        val Subject: Subject
)

