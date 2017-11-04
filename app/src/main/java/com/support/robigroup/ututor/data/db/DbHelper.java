package com.support.robigroup.ututor.data.db;

import java.util.List;

import io.reactivex.Observable;

/**
 * Created by Bimurat Mukhtar on 29.10.2017.
 */

public interface DbHelper {

    Observable<Boolean> isQuestionEmpty();

    Observable<Boolean> isOptionEmpty();

}
