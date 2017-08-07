package com.support.robigroup.ututor.commons;

import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;

import static android.net.sip.SipErrorCode.SERVER_ERROR;

/**
 * Created by Bimurat Mukhtar on 19.04.2017.
 */

public class Constants {

    public static final int BAD_REQUEST=400; // плохой плохой клиент каку написал
    public static final int UNAUTHORIZED=401; // нужен токен
    public static final int FORBIDDEN=403; // запрещенный запрос
    public static final int NOT_FOUND=404; // не найден, not our problem
    public static final int SERVER_ERROR=500; //not our problem

    public Snackbar requestErrorHandler(int code, View parentView) {
        if(code > 200 && code < 300) {
            return null;
        }
        Snackbar snackbar = Snackbar.make(parentView, "YROO XRKSVI GIRZMTOV", Snackbar.LENGTH_LONG);
        switch(code) {
            case BAD_REQUEST:
                snackbar.setText("Client's data is already exist on server or is invalid");
                snackbar.setActionTextColor(Color.RED);
                break;
            case UNAUTHORIZED:
                snackbar.setText("Client is not authorized");
                snackbar.setActionTextColor(Color.RED);
                break;
            case FORBIDDEN:
                snackbar.setText("Forbidden request");
                snackbar.setActionTextColor(Color.RED);
                break;
            case NOT_FOUND:
                snackbar.setText("API not found");
                snackbar.setActionTextColor(Color.RED);
                break;
            case SERVER_ERROR:
                snackbar.setText("Server is experiencing problems");
                snackbar.setActionTextColor(Color.RED);
                break;
        }
        return snackbar;
    }
    //// Backend finished

    public static final String TAG = "myLogs";
}
