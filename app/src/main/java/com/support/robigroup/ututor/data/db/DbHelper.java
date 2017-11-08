package com.support.robigroup.ututor.data.db;

import com.support.robigroup.ututor.commons.ChatInformation;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Observable;

/**
 * Created by Bimurat Mukhtar on 29.10.2017.
 */

public interface DbHelper {

    ChatInformation getChatInformation();

    List<ChatMessage> getChatMessages();

}
