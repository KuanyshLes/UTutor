package com.support.robigroup.ututor.ui.base;

import com.support.robigroup.ututor.commons.ChatInformation;
import com.support.robigroup.ututor.commons.ChatLesson;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Rhyme on 18-Nov-17.
 */

public interface MvpRealmPresenter{

    ChatInformation getChatInformation();

    RealmResults<ChatMessage> getChatMessages();

    void updateChatInformation(ChatLesson chatLesson);

    Realm getRealm();

}
