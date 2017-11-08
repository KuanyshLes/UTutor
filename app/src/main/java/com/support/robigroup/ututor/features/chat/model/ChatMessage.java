package com.support.robigroup.ututor.features.chat.model;

import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;
import com.support.robigroup.ututor.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

public class ChatMessage extends RealmObject implements IMessage, MessageContentType{
    @SerializedName("Id")
    @Expose
    private String Id;
    @SerializedName("Time")
    @Expose
    private String Time;
    @SerializedName("FileIconPath")
    @Expose
    private String FileIconPath;
    @SerializedName("FilePath")
    @Expose
    private String FilePath;
    @SerializedName("Text")
    @Expose
    private String Text;
    @SerializedName("Owner")
    @Expose
    private String Owner;
    @Ignore private ChatUser user;

    public ChatMessage(String id, String time, String fileIconPath, String filePath, String text, String owner) {
        Id = id;
        Time = time;
        FileIconPath = fileIconPath;
        FilePath = filePath;
        Text = text;
        Owner = owner;
        user = new ChatUser(owner,owner,null,true);
    }

    public ChatMessage(){

    }

    @Override
    public String getId() {
        return Id.toString();
    }

    public void setId(String id) {
        Id = id;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFileIconPath() {
        return FileIconPath;
    }

    public String getIconUrl(){
        return Constants.INSTANCE.getBASE_URL()+ getFileIconPath();
    }

    public String getImageUrl(){
        return Constants.INSTANCE.getBASE_URL()+getFilePath();
    }

    public void setFileIconPath(String fileIconPath) {
        FileIconPath = fileIconPath;
    }

    public String getFilePath() {
        return FilePath;
    }

    public void setFilePath(String filePath) {
        FilePath = filePath;
    }

    public String getText() {
        return Text;
    }

    @Override
    public IUser getUser() {
        if(user==null)
            user = new ChatUser(getOwner(),getOwner(),null,true);
        return user;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    @Override
    public Date getCreatedAt() {
        Date currentTime = Calendar.getInstance().getTime();
        try{
            currentTime = new SimpleDateFormat(Constants.INSTANCE.getTIMEFORMAT(), Locale.getDefault()).parse(getTime());
        }catch (ParseException exception){
            Log.e("Error","error occured time parsing");
        }
        return currentTime;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String owner) {
        Owner = owner;
    }
}