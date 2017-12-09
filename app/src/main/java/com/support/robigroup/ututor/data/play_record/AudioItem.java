package com.support.robigroup.ututor.data.play_record;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.annotations.Ignore;


public class AudioItem implements Parcelable {

    private int id;
    private String mName; // file name
    private String mFilePath; //file path
    //private int mId; //id in database
    private long mLength; // length of recording in seconds
    private long mTime; // date/time of the recording

    @Ignore
    public boolean isPlaying = false;
    @Ignore
    public boolean isPaused;
    @Ignore
    public long playProgress;

    public AudioItem() {
    }

    private AudioItem(Parcel in) {
        mName = in.readString();
        mFilePath = in.readString();
        //mId = in.readInt();
        mLength = in.readLong();
        mTime = in.readLong();
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String filePath) {
        mFilePath = filePath;
    }

    public long getLength() {
        return mLength;
    }

    public void setLength(long length) {
        mLength = length;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public static final Parcelable.Creator<AudioItem> CREATOR =
            new Parcelable.Creator<AudioItem>() {
                public AudioItem createFromParcel(Parcel in) {
                    return new AudioItem(in);
                }

                public AudioItem[] newArray(int size) {
                    return new AudioItem[size];
                }
            };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeInt(mId);
        dest.writeLong(mLength);
        dest.writeLong(mTime);
        dest.writeString(mFilePath);
        dest.writeString(mName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

}
