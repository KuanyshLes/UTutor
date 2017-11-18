package com.support.robigroup.ututor.data.db;

import com.support.robigroup.ututor.commons.ChatInformation;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Singleton;
import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


@Singleton
public class AppDbHelper implements DbHelper {
    private Realm realm;

    @Inject
    public AppDbHelper() {
    }

    @Override
    public void initRealm() {
        realm = Realm.getDefaultInstance();
    }

    @Override
    public ChatInformation getChatInformation() {
        return realm.where(ChatInformation.class).findFirst();
    }

    @Override
    public RealmResults<ChatMessage> getChatMessages() {
        return realm.where(ChatMessage.class).findAll();
    }

    @Override
    public Realm getRealm() {
        return realm;
    }
}
