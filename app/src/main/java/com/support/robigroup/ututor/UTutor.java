package com.support.robigroup.ututor;

import android.app.Application;
import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.support.robigroup.ututor.data.DataManager;
import com.support.robigroup.ututor.di.component.ApplicationComponent;
import com.support.robigroup.ututor.di.component.DaggerApplicationComponent;
import com.support.robigroup.ututor.di.module.ApplicationModule;
import com.support.robigroup.ututor.singleton.SingletonSharedPref;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class UTutor extends Application {

    @Inject
    DataManager mDataManager;
    private ApplicationComponent mApplicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

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
}
