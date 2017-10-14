package com.support.robigroup.ututor.features.chat.model;

import com.stfalcon.chatkit.commons.models.IUser;

import io.realm.RealmObject;

public class ChatUser extends RealmObject implements IUser {

    private String id;
    private String name;
    private String avatar;
    private Boolean online;

    public ChatUser(String id, String name, String avatar, Boolean online) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.online = online;
    }

    public ChatUser(){

    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }
}
