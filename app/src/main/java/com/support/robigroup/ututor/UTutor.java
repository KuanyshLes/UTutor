package com.support.robigroup.ututor;

import android.app.Application;
import com.support.robigroup.ututor.singleton.SingletonSharedPref;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class UTutor extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

        SingletonSharedPref.getInstance(getBaseContext());
    }
}
