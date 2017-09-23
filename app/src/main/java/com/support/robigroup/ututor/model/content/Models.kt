package com.support.robigroup.ututor.model.content

import android.os.Parcel
import android.os.Parcelable
import com.support.robigroup.ututor.Constants
import com.support.robigroup.ututor.screen.main.adapters.AdapterConstants
import com.support.robigroup.ututor.screen.main.adapters.ViewType
import com.support.robigroup.ututor.commons.createParcel
import io.realm.RealmObject
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
        var Id: Int,
        var Text: String,
        var ClassNumber: Int
): Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Subject(it) }
    }

    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readInt(),
            parcelIn.readString(),
            parcelIn.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(Id!!)
        dest.writeString(Text)
        dest.writeInt(ClassNumber!!)
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
        var FirstName: String?= null,
        var LastName: String? = null,
        var MiddleName: String? = null,
        var Birthday: String? = null,
        var Speciality: String? = null,
        var Education: String? = null,
        var LessonRequestId: String? = null,
        var FullName: String? = null,
        var ProfilePhoto: String? = null
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
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(Id)
        dest.writeFloat(Raiting ?: 0.0F)
        dest.writeString(Languages)
        dest.writeString(FirstName)
        dest.writeString(LastName)
        dest.writeString(MiddleName)
        dest.writeString(Birthday)
        dest.writeString(Speciality)
        dest.writeString(Education)
        dest.writeString(LessonRequestId)
        dest.writeString(FullName)
        dest.writeString(ProfilePhoto)

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
        var LearnerId: String? = null,
        var TeacherId: String? = null,
        var RequestTime: String? = null,
        var SubjectName: String? = null,
        var Class: Int? = null,
        var Learner: String? = null,
        var Id: String? = null,
        var SubjectId: Int? = null
)

data class LoginResponse(
        val access_token: String,
        val expires_in: Int,
        val FullName: String
)

data class ChatLesson(
        var Id: Int? = null,
        var CreateTime: String = "00:00",
        var StartTime: String? = null,
        var EndTime: String? = null,
        var StatusId: Int = Constants.STATUS_NOT_REQUESTED,
        var Duration: String? = null,
        var TeacherId: String = "",
        var LearnerId: String = "",
        var SubjectName: String = "",
        var Class: Int? = null,
        var Learner: String = "",
        var Teacher: String = "",
        var TeacherReady: Boolean = false,
        var LearnerReady: Boolean = false,
        var LearnerRaiting: Float? = null,
        var TeacherRaiting: Float? = null,
        var SubjectId: Int? = null,
        var Language: String? = null,
        var InvoiceSum: String? = null
)

open class ChatInformation(
        var Id: Int? = null,
        var CreateTime: String = "00:00", //notNull
        var StartTime: String? = null,
        var EndTime: String? = null,
        var StatusId: Int = Constants.STATUS_NOT_REQUESTED,
        var Duration: String? = null,
        var TeacherId: String = "",
        var LearnerId: String = "",
        var SubjectName: String = "",
        var ClassNumber: Int? = null,
        var Learner: String = "",
        var Teacher: String = "",
        var TeacherReady: Boolean = false,
        var LearnerReady: Boolean = false,
        var LearnerRaiting: Float? = null,
        var TeacherRaiting: Float? = null,
        var SubjectId: Int? = null,
        var Language: String? = null,
        var InvoiceSum: String? = null
): RealmObject()

