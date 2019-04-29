
package com.example.dominator.smartparkinginterface.utils;

import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import java.net.InetAddress;
import java.net.Socket;

public class HttpUtils {
    private String BASE_URL;

    public HttpUtils(String BASE_URL) {
        this.BASE_URL = BASE_URL;
    }

    private static AsyncHttpClient client = new AsyncHttpClient();

    public void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(getAbsoluteUrl(url), params, responseHandler);
    }

    public void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(getAbsoluteUrl(url), params, responseHandler);
    }

    public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.get(url, params, responseHandler);
    }

    public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.post(url, params, responseHandler);
    }

    public static void setClient(AsyncHttpClient newClient){
        client = newClient;
    }

    public static AsyncHttpClient getClient(){
        return client;
    }

    public static void clearSocket(int port) {
        try {
            Socket s = new Socket(InetAddress.getLocalHost().getHostName(), port);
            s.setReuseAddress(true);
            //s.close();

        }
        catch(Exception e) {
            Log.d("TAG", e.toString());
        }
    }

    private String getAbsoluteUrl(String relativeUrl) {
        return BASE_URL + relativeUrl;
    }
}
