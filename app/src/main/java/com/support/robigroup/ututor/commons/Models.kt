package com.support.robigroup.ututor.commons

import android.os.Parcel
import android.os.Parcelable
import com.support.robigroup.ututor.Constants
import io.realm.RealmObject


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

data class Type(
        val Id: Int,
        val Name: String,
        var Resource: Int
)


data class Subject(
        var Id: Int,
        var Name: String,
        var ClassNumber: Int,
        var Classes: String
): Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Subject(it) }
    }

    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readInt(),
            parcelIn.readString(),
            parcelIn.readInt(),
            parcelIn.readString()
            )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(Id)
        dest.writeString(Name)
        dest.writeInt(ClassNumber)
        dest.writeString(Classes)
    }

    override fun describeContents() = 0
}

data class TopicItem(
        val Id: Int = 0,
        var Text: String? = null,
        var subject: Subject? = null
) : Parcelable {

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
        dest.writeInt(Id)
        dest.writeString(Text ?: "error")
        dest.writeParcelable(subject,flags)
    }

    override fun describeContents() = 0
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
        var ProfilePhoto: String? = null,
        var LessonRequestClass: Int? = null,
        var LessonRequestSubjectId: Int? = null,
        var LessonRequestSubjectName: String? = null,
        var LessonRequestLanguage: String? = null
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
        val FullName: String,
        val Role: String
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
        var InvoiceSum: String? = null,
        var InvoiceTariff: String? = null
)

data class ChatHistory(
        var Id: Int? = null,
        var CreateTime: String? = null,
        var StartTime: String? = null,
        var EndTime: String? = null,
        var Class: Int? = null,
        var Duration: String? = null,
        var LearnerRaiting: Float? = null,
        var TeacherRaiting: Float? = null,
        var Language: String? = null,
        var SubjectName: String = "",
        var ChatUserName: String? = null,
        var ChatUserProfilePhoto: String? = null,
        var InvoiceSum: String? = null,
        var InvoiceTariff: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.readValue(Float::class.java.classLoader) as? Float,
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(Id)
        parcel.writeString(CreateTime)
        parcel.writeString(StartTime)
        parcel.writeString(EndTime)
        parcel.writeValue(Class)
        parcel.writeString(Duration)
        parcel.writeValue(LearnerRaiting)
        parcel.writeValue(TeacherRaiting)
        parcel.writeString(Language)
        parcel.writeString(SubjectName)
        parcel.writeString(ChatUserName)
        parcel.writeString(ChatUserProfilePhoto)
        parcel.writeString(InvoiceSum)
        parcel.writeString(InvoiceTariff)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatHistory> {
        override fun createFromParcel(parcel: Parcel): ChatHistory {
            return ChatHistory(parcel)
        }

        override fun newArray(size: Int): Array<ChatHistory?> {
            return arrayOfNulls(size)
        }
    }
}

open class ChatInformation(
        var Id: Int? = null,
        var CreateTime: String = "00:00",
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
        var InvoiceSum: String? = null,
        var InvoiceTariff: String? = null,
        var deviceCreateTime: String? = null
): RealmObject()

data class Profile(
        var Balance: Double? = null,
        var Language: String? = null,
        var Education: String? = null,
        var Class: String? = null,
        var Email: String? = null,
        var FirstName: String? = null,
        var LastName: String? = null,
        var MiddleName: String? = null,
        var ProfilePhotoPath: String? = null,
        var FullName: String? = null,
        var Birthday: String? = null
)

data class Language(
        var flagIcon: Int,
        var text: String,
        var request: String,
        var status: Boolean = false
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(flagIcon)
        parcel.writeString(text)
        parcel.writeString(request)
        parcel.writeByte(if (status) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Language> {
        override fun createFromParcel(parcel: Parcel): Language {
            return Language(parcel)
        }

        override fun newArray(size: Int): Array<Language?> {
            return arrayOfNulls(size)
        }
    }
}

