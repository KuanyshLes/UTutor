package com.support.robigroup.ututor;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.support.robigroup.ututor.data.DataManager;
import com.support.robigroup.ututor.di.component.ApplicationComponent;
import com.support.robigroup.ututor.di.component.DaggerApplicationComponent;
import com.support.robigroup.ututor.di.module.ApplicationModule;
import com.support.robigroup.ututor.singleton.SingletonSharedPref;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class UTutor extends Application {

    public ApplicationComponent component;

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

        Fresco.initialize(this);

        SingletonSharedPref.getInstance(getBaseContext());

        mApplicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this)).build();

        mApplicationComponent.inject(this);
    }

    public static UTutor get(Context context){
        return (UTutor) context.getApplicationContext();
    }

    public ApplicationComponent getComponent(){ return component;}
}
