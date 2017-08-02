package com.support.robigroup.ututor;

import android.app.Application;
import android.support.annotation.NonNull;

import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.rx.RealmObservableFactory;

/**
 * @author Artur Vasilov
 */
public class UTutor extends Application {

    private static UTutor sInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Picasso picasso = new Picasso.Builder(this)
                .downloader(new OkHttp3Downloader(this))
                .build();
        Picasso.setSingletonInstance(picasso);

        Realm.init(this);

//        RealmConfiguration configuration = new RealmConfiguration.Builder()
//                .rxFactory(new RealmObservableFactory())
//                .build();
//        Realm.setDefaultConfiguration(configuration);
    }

    @NonNull
    public static UTutor getAppContext() {
        return sInstance;
    }
}
