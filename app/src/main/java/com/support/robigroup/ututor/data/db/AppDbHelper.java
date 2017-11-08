package com.support.robigroup.ututor.data.db;

import com.support.robigroup.ututor.commons.ChatInformation;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import io.realm.Realm;


@Singleton
public class AppDbHelper implements DbHelper {
    private Realm realm;

    @Inject
    public AppDbHelper() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public ChatInformation getChatInformation() {
        return realm.where(ChatInformation.class).findFirst();
    }

    @Override
    public List<ChatMessage> getChatMessages() {
        return realm.where(ChatMessage.class).findAll();
    }

}
