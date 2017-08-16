package com.support.robigroup.ututor;

import android.app.Application;
import android.support.annotation.NonNull;

import com.support.robigroup.ututor.singleton.SingletonSharedPref;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * @author Artur Vasilov
 */
public class UTutor extends Application {

    private static UTutor sInstance;


    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        SingletonSharedPref.getInstance(getBaseContext());

//        RealmConfiguration configuration = new RealmConfiguration.Builder()
//                .rxFactory(new RealmObservableFactory())
//                .build();
//        Realm.setDefaultConfiguration(configuration);
    }
}
