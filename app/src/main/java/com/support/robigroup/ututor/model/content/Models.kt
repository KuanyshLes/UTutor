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
        var after: String = "",
        var before: String = "",
        var name: String = "Mathematica",
        var news: List<TopicItem> = ArrayList()
): Parcelable {

    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Lesson(it) }
    }

    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readString(),
            mutableListOf<TopicItem>().apply {
                parcelIn.readTypedList(this, TopicItem.CREATOR)
            }
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(after)
        dest.writeString(before)
        dest.writeString(name)
        dest.writeTypedList(news)
    }

    override fun describeContents() = 0

}

data class TopicItem(
        var lesson: String = "",
        var author: String = "",
        var description: String = "",
        var rating: Double = 0.0,
        var created: Long = 0,
        var id: String = "",
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
        dest.writeString(description)
        dest.writeDouble(rating)
        dest.writeLong(created)
        dest.writeString(id)
        dest.writeInt(group)
    }

    override fun describeContents() = 0

    override fun getViewType() = AdapterConstants.TOPICS
}

data class Teacher(
        var name: String? = null,
        var imagePath: String? = null,
        var rating: Double? = null,
        var lessons: List<Lesson> = ArrayList()
): Parcelable{
    companion object {
        @JvmField @Suppress("unused")
        val CREATOR = createParcel { Teacher(it) }
    }
    protected constructor(parcelIn: Parcel) : this(
            parcelIn.readString(),
            parcelIn.readString(),
            parcelIn.readDouble(),
            mutableListOf<Lesson>().apply {
                parcelIn.readTypedList(this, Lesson.CREATOR)
            }
    )

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(name)
        dest.writeString(imagePath)
        dest.writeDouble(rating ?: 0.0)
        dest.writeTypedList(lessons)
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