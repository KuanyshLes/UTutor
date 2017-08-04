package com.support.robigroup.ututor.model.content

import android.os.Parcel
import android.os.Parcelable
import com.support.robigroup.ututor.commons.adapter.AdapterConstants
import com.support.robigroup.ututor.commons.adapter.ViewType
import com.support.robigroup.ututor.commons.createParcel

/**
 * Created by Bimurat Mukhtar on 03.08.2017.
 */
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
        var name: String = "",
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
        var title: String = "",
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
        dest.writeString(title)
        dest.writeDouble(rating)
        dest.writeLong(created)
        dest.writeString(id)
        dest.writeInt(group)
    }

    override fun describeContents() = 0

    override fun getViewType() = AdapterConstants.TOPICS
}
