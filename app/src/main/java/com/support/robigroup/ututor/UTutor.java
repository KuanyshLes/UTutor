package com.support.robigroup.ututor;

import android.app.Application;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.support.robigroup.ututor.data.DataManager;
import com.support.robigroup.ututor.di.component.ApplicationComponent;
import com.support.robigroup.ututor.di.component.DaggerApplicationComponent;
import com.support.robigroup.ututor.di.module.ApplicationModule;
import com.support.robigroup.ututor.singleton.SingletonSharedPref;

import javax.inject.Inject;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class UTutor extends Application {

    @Inject
    DataManager mDataManager;
    private ApplicationComponent mApplicationComponent;
    private Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();
        configureCrashReporting();

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realm = Realm.getDefaultInstance();

        AndroidNetworking.initialize(getApplicationContext());

        ImagePipelineConfig imageConfig = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, imageConfig);

        SingletonSharedPref.getInstance(getBaseContext());

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);
    }

    public static UTutor get(Context context){
        return (UTutor) context.getApplicationContext();
    }

    public ApplicationComponent getComponent(){ return mApplicationComponent;}

    private void configureCrashReporting() {
        CrashlyticsCore crashlyticsCore = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(crashlyticsCore).build());
    }
}
