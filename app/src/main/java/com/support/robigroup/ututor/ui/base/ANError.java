package com.support.robigroup.ututor.ui.base;

import com.google.gson.Gson;
import okhttp3.Response;

@SuppressWarnings({"unchecked", "unused"})
public class ANError extends Exception {

    public static final String REQUEST_CANCELLED_ERROR = "requestCancelledError";

    private String errorBody;

    private int errorCode = 0;

    private String errorDetail;

    private Response response;

    public ANError() {
    }

    public ANError(Response response) {
        this.response = response;
    }

    public ANError(String message) {
        super(message);
    }

    public ANError(String message, Response response) {
        super(message);
        this.response = response;
    }

    public ANError(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ANError(String message, Response response, Throwable throwable) {
        super(message, throwable);
        this.response = response;
    }

    public ANError(Response response, Throwable throwable) {
        super(throwable);
        this.response = response;
    }

    public ANError(Throwable throwable) {
        super(throwable);
    }

    public Response getResponse() {
        return response;
    }

    public void setErrorDetail(String errorDetail) {
        this.errorDetail = errorDetail;
    }

    public String getErrorDetail() {
        return this.errorDetail;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public void setCancellationMessageInError() {
        this.errorDetail = REQUEST_CANCELLED_ERROR;
    }

    public String getErrorBody() {
        return errorBody;
    }

    public void setErrorBody(String errorBody) {
        this.errorBody = errorBody;
    }

    public <T> T getErrorAsObject(Class<T> objectClass) {
        try {
            return (new Gson()).fromJson(errorBody, objectClass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
