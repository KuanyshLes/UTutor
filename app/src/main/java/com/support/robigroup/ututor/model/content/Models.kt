package com.support.robigroup.ututor.model.content

import android.os.Parcel
import android.os.Parcelable
import com.support.robigroup.ututor.commons.adapter.AdapterConstants
import com.support.robigroup.ututor.commons.adapter.ViewType
import com.support.robigroup.ututor.commons.createParcel
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey


data class ClassRoom(
        var subjects: List<Subject> = ArrayList()
): Parcelable{
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { ClassRoom(it) }
    }
    protected constructor(parcelIn: Parcel) : this(
            mutableListOf<Subject>().apply {
                parcelIn.readTypedList(this, Subject.CREATOR)
            }
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(subjects)
    }

    override fun describeContents() = 0
}


data class Subject(
        var Id: Int? = null,
        var Text: String? = null,
        var topics: List<TopicItem> = ArrayList()
): Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Subject(it) }
    }

    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readInt(),
            parcelIn.readString(),
            mutableListOf<TopicItem>().apply {
                parcelIn.readTypedList(this, TopicItem.CREATOR)
            }
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(Id!!)
        dest.writeString(Text)
        dest.writeTypedList(topics)
    }

    override fun describeContents() = 0

}

data class TopicItem(
        var language: String? = null,
        var Text: String? = null,
        var rating: Double? = null,
        var created: Long? = null,
        var subjectId: Int? = null,
        val Id: Int? = 0,
        var classRoom: Int? = null
) : ViewType, Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { TopicItem(it) }
    }

    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readDouble(),
            parcelIn.readLong(),
            parcelIn.readInt(),
            parcelIn.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(language ?: "error")
        dest.writeString(Text ?: "error")
        dest.writeDouble(rating ?: 0.0)
        dest.writeLong(created ?: 0L)
        dest.writeInt(Id ?: 0)
        dest.writeInt(classRoom ?: 0)
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
        var Image: String? = null
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

data class Lesson(
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

open class RequestListen(
        @PrimaryKey var Id: Int = 0,
        var status: Int = 0
): RealmObject()

data class LoginResponse(
        val access_token: String,
        val expires_in: Int
)