package com.shakk.travlendar;

import com.loopj.android.http.*;

import java.security.Principal;

import cz.msebera.android.httpclient.auth.AuthScope;
import cz.msebera.android.httpclient.auth.BasicUserPrincipal;
import cz.msebera.android.httpclient.auth.Credentials;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TravlendarRestClient {

    private static final String BASE_URL = "http://151.236.60.56:8080/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public TravlendarRestClient() {
        client.setTimeout(20 * 1000);
        client.setResponseTimeout(20 * 1000);
        client.setConnectTimeout(20 * 1000);
    }

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void get(String url, String authorization, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer ".concat(authorization));
        client.get(getAbsoluteUrl(url), responseHandler);
    }

    public static void post(String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    public static void post(String url, String authorization
                            , StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Authorization", "Bearer ".concat(authorization));
        client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
