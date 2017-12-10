package com.shakk.travlendar;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.entity.StringEntity;

public class TravlendarRestClient {

    private static final String BASE_URL = "http://151.236.60.56:8080/ApplicationServerArchive/";

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void post(String url, StringEntity entity, AsyncHttpResponseHandler responseHandler) {
        client.addHeader("Postman-Token", "e1c8684d-fab0-c724-12a9-e2909789b174");
        //client.addHeader("Content-Type", "application/json");
        //client.addHeader("Host", "151.236.60.56:8080");
        client.addHeader("Cache-Control", "no-cache");
        client.setTimeout(20 * 1000);
        client.setResponseTimeout(20 * 1000);
        client.setConnectTimeout(20 * 1000);
        client.post(null, getAbsoluteUrl(url), entity, "application/json", responseHandler);
    }

    private static String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
