package com.bondex.library.net;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;


/**
 * date: 2020/5/6
 *
 * @Author: ysl
 * description:
 */
public class HttpUrl {

    public static OkHttpClient CLIENT;
    private static String BASE_URL = "";


    private static OkHttpClient getInstance() {

        if (CLIENT == null) {
            CLIENT = new OkHttpClient.Builder().build();
        }

        return CLIENT;
    }


    public void httpUtil(RequestBody body, Callback callback) throws IOException {


        Request request = new Request.Builder()
                .url(BASE_URL)
                .post(body)
                .build();

        getInstance().newCall(request).enqueue(callback);

    }


}
