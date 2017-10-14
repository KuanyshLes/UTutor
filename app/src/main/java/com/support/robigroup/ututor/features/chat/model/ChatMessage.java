package com.support.robigroup.ututor.features.chat.model;

import android.util.Log;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.commons.models.MessageContentType;
import com.support.robigroup.ututor.Constants;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;

public class ChatMessage extends RealmObject implements IMessage, MessageContentType{
    private Integer Id;
    private String Time;
    private String FilePathIcon;
    private String FilePath;
    private String Text;
    private String Owner;
    private Date created;
    private ChatUser user;

    public ChatMessage(Integer id, String time, String filePathIcon, String filePath, String text, String owner) {
        Id = id;
        Time = time;
        FilePathIcon = filePathIcon;
        FilePath = filePath;
        Text = text;
        Owner = owner;
        user = new ChatUser(owner,owner,null,true);
    }

    public ChatMessage(){

    }

    @Override
    public String getId() {
        return "";
    }

    public void setId(Integer id) {
        Id = id;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getFilePathIcon() {
        return FilePathIcon;
    }

    public void setFilePathIcon(String filePathIcon) {
        FilePathIcon = filePathIcon;
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
        return null;
    }

    public void setUser(ChatUser user) {
        this.user = user;
    }

    @Override
    public Date getCreatedAt() {
        Date currentTime = Calendar.getInstance().getTime();
        try{
            currentTime = new SimpleDateFormat(Constants.INSTANCE.getTIMEFORMAT()).parse(getTime());
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
