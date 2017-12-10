package com.support.robigroup.ututor.data;

import com.support.robigroup.ututor.api.APIInterface;
import com.support.robigroup.ututor.data.network.NetworkHelper;
import com.support.robigroup.ututor.data.prefs.PreferencesHelper;



public interface DataManager extends  PreferencesHelper, NetworkHelper {

    void setUserAsLoggedOut();

    APIInterface getApiHelper();



    void updateUserInfo(
            String accessToken,
            Long userId,
            LoggedInMode loggedInMode,
            String userName,
            String email,
            String profilePicPath);

    enum LoggedInMode {

        LOGGED_IN_MODE_LOGGED_OUT(0),
        LOGGED_IN_MODE_GOOGLE(1),
        LOGGED_IN_MODE_FB(2),
        LOGGED_IN_MODE_SERVER(3);

        private final int mType;

        LoggedInMode(int type) {
            mType = type;
        }

        public int getType() {
            return mType;
        }
    }
}
