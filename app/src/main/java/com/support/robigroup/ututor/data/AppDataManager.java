package com.support.robigroup.ututor.data;

import android.content.Context;
import android.support.annotation.NonNull;

import com.support.robigroup.ututor.api.APIInterface;
import com.support.robigroup.ututor.api.RestAPI;
import com.support.robigroup.ututor.data.file.FileHelper;
import com.support.robigroup.ututor.data.network.NetworkHelper;
import com.support.robigroup.ututor.data.prefs.PreferencesHelper;
import com.support.robigroup.ututor.di.ApplicationContext;
import com.support.robigroup.ututor.features.chat.model.ChatMessage;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Flowable;
import retrofit2.Response;


@Singleton
public class AppDataManager implements DataManager{

    private static final String TAG = "AppDataManager";
    private final Context mContext;
    private final PreferencesHelper mPreferencesHelper;
    private final NetworkHelper mNetworkHelper;
    private final FileHelper mFileHelper;

    @Inject
    public AppDataManager(@ApplicationContext Context context,
                          PreferencesHelper preferencesHelper,
                          NetworkHelper networkHelper,
                          FileHelper fileHelper) {
        mContext = context;
        mPreferencesHelper = preferencesHelper;
        mNetworkHelper = networkHelper;
        mFileHelper = fileHelper;
    }

    @NonNull
    @Override
    public Flowable<Response<ChatMessage>> sendAudioMessage(File file) {
        return mNetworkHelper.sendAudioMessage(file);
    }

    @NotNull
    @Override
    public Flowable<Response<ChatMessage>> sendImageTextMessage(@Nullable String messageText, @Nullable String file64base) {
        return mNetworkHelper.sendImageTextMessage(messageText, file64base);
    }

    @Override
    public Flowable<Response<List<ChatMessage>>> getChatMessages(String chatId) {
        return mNetworkHelper.getChatMessages(chatId);
    }

    @Override
    public String getAccessToken() {
        return mPreferencesHelper.getAccessToken();
    }

    @Override
    public void setAccessToken(String accessToken) {
        mPreferencesHelper.setAccessToken(accessToken);
    }

    @Override
    public int getCurrentUserLoggedInMode() {
        return mPreferencesHelper.getCurrentUserLoggedInMode();
    }

    @Override
    public void setCurrentUserLoggedInMode(LoggedInMode mode) {
        mPreferencesHelper.setCurrentUserLoggedInMode(mode);
    }

    @Override
    public Long getCurrentUserId() {
        return mPreferencesHelper.getCurrentUserId();
    }

    @Override
    public void setCurrentUserId(Long userId) {
        mPreferencesHelper.setCurrentUserId(userId);
    }

    @Override
    public String getCurrentUserName() {
        return mPreferencesHelper.getCurrentUserName();
    }

    @Override
    public void setCurrentUserName(String userName) {
        mPreferencesHelper.setCurrentUserName(userName);
    }

    @Override
    public String getCurrentUserEmail() {
        return mPreferencesHelper.getCurrentUserEmail();
    }

    @Override
    public void setCurrentUserEmail(String email) {
        mPreferencesHelper.setCurrentUserEmail(email);
    }

    @Override
    public String getCurrentUserProfilePicUrl() {
        return mPreferencesHelper.getCurrentUserProfilePicUrl();
    }

    @Override
    public void setCurrentUserProfilePicUrl(String profilePicUrl) {
        mPreferencesHelper.setCurrentUserProfilePicUrl(profilePicUrl);
    }

    @Override
    public void updateUserInfo(
            String accessToken,
            Long userId,
            LoggedInMode loggedInMode,
            String userName,
            String email,
            String profilePicPath) {

        setAccessToken(accessToken);
        setCurrentUserId(userId);
        setCurrentUserLoggedInMode(loggedInMode);
        setCurrentUserName(userName);
        setCurrentUserEmail(email);
        setCurrentUserProfilePicUrl(profilePicPath);
    }

    @Override
    public APIInterface getApiHelper() {
        return RestAPI.Companion.getApi();
    }

    @Override
    public void setUserAsLoggedOut() {
        updateUserInfo(
                null,
                null,
                DataManager.LoggedInMode.LOGGED_IN_MODE_LOGGED_OUT,
                null,
                null,
                null);
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
}
