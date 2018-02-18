package com.support.robigroup.ututor.di.module;

import android.app.Application;
import android.content.Context;

import com.support.robigroup.ututor.data.AppDataManager;
import com.support.robigroup.ututor.data.DataManager;
import com.support.robigroup.ututor.data.file.AppFileHelper;
import com.support.robigroup.ututor.data.file.FileHelper;
import com.support.robigroup.ututor.data.network.AppNetworkHelper;
import com.support.robigroup.ututor.data.network.NetworkHelper;
import com.support.robigroup.ututor.data.prefs.AppPreferencesHelper;
import com.support.robigroup.ututor.data.prefs.PreferencesHelper;
import com.support.robigroup.ututor.di.ApplicationContext;
import com.support.robigroup.ututor.di.DatabaseInfo;
import com.support.robigroup.ututor.di.PreferenceInfo;
import com.support.robigroup.ututor.utils.AppConstants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @DatabaseInfo
    String provideDatabaseName() {
        return AppConstants.DB_NAME;
    }


    @Provides
    @PreferenceInfo
    String providePreferenceName() {
        return AppConstants.PREF_NAME;
    }

    @Provides
    @Singleton
    DataManager provideDataManager(AppDataManager appDataManager) {
        return appDataManager;
    }

    @Provides
    @Singleton
    PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }

    @Provides
    @Singleton
    NetworkHelper provideNetworkHelper(AppNetworkHelper appNetworkHelper) {
        return appNetworkHelper;
    }


    @Provides
    @Singleton
    FileHelper provideFileHelper(AppFileHelper appFileHelper) {
        return appFileHelper;
    }
}
