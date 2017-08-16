package com.support.robigroup.ututor.model.content

import android.os.Parcel
import android.os.Parcelable
import com.support.robigroup.ututor.commons.adapter.AdapterConstants
import com.support.robigroup.ututor.commons.adapter.ViewType
import com.support.robigroup.ututor.commons.createParcel


data class ClassRoom(
        var lessons: List<Lesson> = ArrayList()
): Parcelable{
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { ClassRoom(it) }
    }
    protected constructor(parcelIn: Parcel) : this(
            mutableListOf<Lesson>().apply {
                parcelIn.readTypedList(this, Lesson.CREATOR)
            }
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeTypedList(lessons)
    }

    override fun describeContents() = 0
}


data class Lesson(
        var Id: Int? = null,
        var Text: String? = null,
        var topics: List<TopicItem> = ArrayList()
): Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Lesson(it) }
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
        var lesson: String = "",
        var author: String = "",
        var Text: String = "",
        var rating: Double = 0.0,
        var created: Long = 0,
        var Id: String = "",
        var group: Int = 0
) : ViewType, Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { TopicItem(it) }
    }

    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readDouble(),
            parcelIn.readLong(),
            parcelIn.readString(),
            parcelIn.readInt()
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(lesson)
        dest.writeString(author)
        dest.writeString(Text)
        dest.writeDouble(rating)
        dest.writeLong(created)
        dest.writeString(Id)
        dest.writeInt(group)
    }

    override fun describeContents() = 0

    override fun getViewType() = AdapterConstants.TOPICS
}

data class Teacher(
        val Id: String,
        var rating: Float? = null,
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
        dest.writeFloat(rating ?: 0.0F)
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