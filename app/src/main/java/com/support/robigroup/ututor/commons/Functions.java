package com.support.robigroup.ututor.commons;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;

import com.support.robigroup.ututor.screen.main.MainActivity;

import static com.support.robigroup.ututor.commons.Constants.BAD_REQUEST;
import static com.support.robigroup.ututor.commons.Constants.FORBIDDEN;
import static com.support.robigroup.ututor.commons.Constants.NOT_FOUND;
import static com.support.robigroup.ututor.commons.Constants.SERVER_ERROR;
import static com.support.robigroup.ututor.commons.Constants.UNAUTHORIZED;


public class Functions {

    static Context context;
    private static ProgressDialog progressDialog;
    public static final int GPS_PROVIDER_REQUEST = 10;

    public static void setContext(Context ctx) {
        context = ctx;
        progressDialog = new ProgressDialog(context);
    }

    public static void builtAlertMessageWithText(String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void builtMessageNoInternet(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Проверьте интернет соединение!")
                .setCancelable(true)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog,
                                        @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void messageOrderExists(){
        builtAlertMessageWithText("Вы уже создали заказ, удалите его и попробуйте снова!");
    }

    public static boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void builtMessageWait(){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Wait");
        progressDialog.setTitle("Sending...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void cancelProgressDialog(){
        if(progressDialog.isShowing()){
            progressDialog.cancel();
        }
    }

    public static boolean errorHandler(int code){

        Functions.cancelProgressDialog();

        if(code >= 200 && code < 300) {
            return true;
        }
        Snackbar snackbar = Snackbar.make(((MainActivity)context).findViewById(android.R.id.content)
                , "YROO XRKSVI GIRZMTOV", Snackbar.LENGTH_LONG);
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
        snackbar.show();
        return false;
    }

    public static boolean errorHandler(int code,String message){

        Functions.cancelProgressDialog();
        if(code >= 200 && code < 300) {
            return true;
        }
        Snackbar snackbar = Snackbar.make(((MainActivity)context).findViewById(android.R.id.content)
                , message, Snackbar.LENGTH_LONG);
        switch(code) {
            case BAD_REQUEST:
                snackbar.setText(message);
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
        snackbar.show();
        return false;
    }
}
