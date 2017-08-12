package com.support.robigroup.ututor.commons;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;



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
}
