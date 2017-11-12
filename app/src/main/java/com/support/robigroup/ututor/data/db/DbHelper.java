package com.support.robigroup.ututor.data.db;

import com.support.robigroup.ututor.commons.ChatInformation;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Bimurat Mukhtar on 29.10.2017.
 */

public interface DbHelper {

    ChatInformation getChatInformation();

    RealmList<ChatMessage> getChatMessages();

    Realm getRealm();

}
