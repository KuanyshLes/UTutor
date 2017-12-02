package com.support.robigroup.ututor.orazbai;

/**
 * Created by orazbay on 03.09.17.
 */

public interface AudioPlayerCallback {
    public void onNewPlay();
    public void onProgressChanged(long cDur, long tDur);
    void onComplete();
    void onReady(int duration);
}
