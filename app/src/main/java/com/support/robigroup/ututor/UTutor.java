package com.support.robigroup.ututor;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.support.robigroup.ututor.singleton.SingletonSharedPref;
import io.realm.Realm;
import io.realm.RealmConfiguration;


public class UTutor extends Application {

    public MyComponent component;

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
    }

    public static UTutor get(Context context){
        return (UTutor) context.getApplicationContext();
    }

    public MyComponent getComponent(){ return component;}
}
