package com.support.robigroup.ututor.data.db;

import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.Observable;

/**
 * Created by Bimurat Mukhtar on 29.10.2017.
 */

@Singleton
public class AppDbHelper implements DbHelper {

//    @Inject
//    public AppDbHelper(DbOpenHelper dbOpenHelper) {
//        mDaoSession = new DaoMaster(dbOpenHelper.getWritableDb()).newSession();
//    }

//    @Override
//    public Observable<Long> insertUser(final User user) {
//        return Observable.fromCallable(new Callable<Long>() {
//            @Override
//            public Long call() throws Exception {
//                return mDaoSession.getUserDao().insert(user);
//            }
//        });
//    }


}
