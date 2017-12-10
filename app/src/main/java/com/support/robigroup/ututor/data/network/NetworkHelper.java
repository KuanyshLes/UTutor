package com.support.robigroup.ututor.data.network;

import com.support.robigroup.ututor.features.chat.model.ChatMessage;

import java.io.File;
import java.util.List;

import io.reactivex.Flowable;
import retrofit2.Response;


public interface NetworkHelper {

    Flowable<Response<ChatMessage>> sendAudioMessage(File file);

    Flowable<Response<ChatMessage>> sendImageMessage();

    Flowable<Response<List<ChatMessage>>> getChatMessages(String chatId);

}
