package com.support.robigroup.ututor.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.support.robigroup.ututor.api.APIInterface;
import com.support.robigroup.ututor.api.RestAPI;
import com.support.robigroup.ututor.data.file.FileHelper;
import com.support.robigroup.ututor.data.network.NetworkHelper;
import com.support.robigroup.ututor.di.ApplicationContext;
import com.support.robigroup.ututor.singleton.SingletonSharedPref;
import com.support.robigroup.ututor.ui.chat.model.ChatMessage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Single;
import retrofit2.Response;


@Singleton
public class AppDataManager implements DataManager{

    private static final String TAG = "AppDataManager";
    private final Context mContext;
    private final NetworkHelper mNetworkHelper;
    private final FileHelper mFileHelper;
    private final SingletonSharedPref mSingletonSharedPref;

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          NetworkHelper networkHelper,
                          FileHelper fileHelper) {
        mContext = context;
        mNetworkHelper = networkHelper;
        mFileHelper = fileHelper;
        mSingletonSharedPref = SingletonSharedPref.getInstance();
    }

    @NonNull
    @Override
    public Single<Response<ChatMessage>> sendAudioMessage(File file) {
        return mNetworkHelper.sendAudioMessage(file);
    }

    @NotNull
    @Override
    public Single<Response<ChatMessage>> sendImageTextMessage(@Nullable String messageText, @Nullable String file64base) {
        return mNetworkHelper.sendImageTextMessage(messageText, file64base);
    }

    @Override
    public Single<Response<List<ChatMessage>>> getChatMessages(String chatId) {
        return mNetworkHelper.getChatMessages(chatId);
    }

    @Override
    public APIInterface getApiHelper() {
        return RestAPI.Companion.getApi();
    }

    @Override
    public void setUserAsLoggedOut() {
        mSingletonSharedPref.clear();
    }

    public SingletonSharedPref getSharedPreferences() {
        return mSingletonSharedPref;
    }

    @NotNull
    @Override
    public String getSentSavePath(@NotNull String chatId) {
        return mFileHelper.getSentSavePath(chatId);
    }

    @NotNull
    @Override
    public String getDownloadSavePath(@NotNull String messageId) {
        return mFileHelper.getDownloadSavePath(messageId);
    }

    @Override
    public boolean checkMessageFileExistance(@NotNull String messageId) {
        return mFileHelper.checkMessageFileExistance(messageId);
    }

    @NotNull
    @Override
    public String getDownloadSaveDir() {
        return mFileHelper.getDownloadSaveDir();
    }

    @NotNull
    @Override
    public String getDownloadFileName(@NotNull String messageId) {
        return mFileHelper.getDownloadFileName(messageId);
    }

    @Override
    public boolean checkFileExistance(@NotNull String url) {
        return mFileHelper.checkFileExistance(url);
    }

    @Override
    public void cleanDirectories() {
        mFileHelper.cleanDirectories();
    }

    @Override
    public void removeFile(@NotNull String path) {
        mFileHelper.removeFile(path);
    }

    @Override
    public int getDurationOfAudioInMillis(@NotNull String path) {
        return mFileHelper.getDurationOfAudioInMillis(path);
    }
}
