package com.support.robigroup.ututor.model.content

import android.os.Parcel
import android.os.Parcelable
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.screen.main.adapters.AdapterConstants
import com.support.robigroup.ututor.screen.main.adapters.ViewType
import com.support.robigroup.ututor.commons.createParcel
import com.support.robigroup.ututor.screen.chat.model.CustomMessage
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey


data class ClassRoom(
        val number: Int
): Parcelable{
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { ClassRoom(it) }
    }
    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(number)
    }
    override fun describeContents() = 0
}


data class Subject(
        var Id: Int? = null,
        var Text: String? = null,
        var classNumber: Int? = null,
        var topics: List<TopicItem> = ArrayList()
): Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Subject(it) }
    }

    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readInt(),
            parcelIn.readString(),
            parcelIn.readInt(),
            mutableListOf<TopicItem>().apply {
                parcelIn.readTypedList(this, TopicItem.CREATOR)
            }
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(Id!!)
        dest.writeString(Text)
        dest.writeInt(classNumber!!)
        dest.writeTypedList(topics)
    }

    override fun describeContents() = 0

}

data class TopicItem(
        val Id: Int = 0,
        var Text: String? = null,
        var subject: Subject? = null
) : ViewType, Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { TopicItem(it) }
    }

    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readInt(),
            parcelIn.readString(),
            parcelIn.readParcelable<Subject>(Subject::class.java.classLoader)
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(Id ?: 0)
        dest.writeString(Text ?: "error")
        dest.writeParcelable(subject,flags)
    }

    override fun describeContents() = 0

    override fun getViewType() = AdapterConstants.TOPICS
}

data class Teacher(
        val Id: String,
        var Raiting: Float? = null,
        var Languages: String? = null,
        var Classes: String? = null,
        var FirstName: String?= null,
        var LastName: String? = null,
        var MiddleName: String? = null,
        var Birthday: String? = null,
        var Image: String? = null,
        var chatInformation: ChatInformation? = null
): Parcelable{
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Teacher(it) }
    }
    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readString(),
            parcelIn.readFloat(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(Id)
        dest.writeFloat(Raiting ?: 0.0F)
        dest.writeString(Languages)
        dest.writeString(Classes)
        dest.writeString(FirstName)
        dest.writeString(LastName)
        dest.writeString(MiddleName)
        dest.writeString(Birthday)
        dest.writeString(Image)
    }

    override fun describeContents() = 0
}

data class Teachers(
        var teachers: List<Teacher> = ArrayList()
): Parcelable{
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Teachers(it) }
    }
    protected constructor(parcelIn: Parcel) : this(
            mutableListOf<Teacher>().apply {
                parcelIn.readTypedList(this, Teacher.CREATOR)
            }
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(teachers)
    }

    override fun describeContents() = 0
}

data class LessonRequestForTeacher(
        val LearnerId: String,
        val TeacherId: String,
        var RequestTime: String,
        val SubjectName: String,
        val Class: Int,
        val Learner: String,
        val TopicId: Int,
        val TopicTitle: String,
        val Id: String
)

data class LoginResponse(
        val access_token: String,
        val expires_in: Int
)

open class RequestListen(
        @PrimaryKey var Id: Int = 0,
        var status: Int = 0
): RealmObject()


data class ChatLesson(
        val Id: Int = 0,
        var TopicId: Int = 0,
        var CreateTime: String? = null,
        var StartTime: String? = null,
        var EndTime: String? = null,
        var StatusId: Int = 0,
        var Duration: String? = null,
        var TeacherId: String = "",
        var LearnerId: String = "",
        var SubjectName: String = "",
        var TopicTitle: String = "",
        var Learner: String = "",
        var Teacher: String = "",
        var TeacherReady: Boolean = false,
        var LearnerReady: Boolean = false,
        var Class: Int = 0
)

open class ChatInformation(
        var Id: Int? = null,
        var TopicId: Int? = null,
        var RequestTime: String? = null,
        var CreateTime: String? = null,
        var StartTime: String? = null,
        var EndTime: String? = null,
        var StatusId: Int = Constants.STATUS_NOT_REQUESTED,
        var Duration: String? = null,
        var TeacherId: String = "",
        var LearnerId: String = "",
        var SubjectName: String = "",
        var TopicTitle: String = "",
        var Learner: String = "",
        var Teacher: String = "",
        var TeacherReady: Boolean = false,
        var LearnerReady: Boolean = false,
        var ClassNumber: Int? = null
): RealmObject()

