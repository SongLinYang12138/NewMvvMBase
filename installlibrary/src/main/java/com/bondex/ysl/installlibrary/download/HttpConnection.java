package com.bondex.ysl.installlibrary.download;


import android.util.Log;

import com.google.gson.Gson;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class HttpConnection {

    private static final String BASE_URL = "http://wol.bondex.com.cn:8089/";
    private static final String VERSION_URL = "http://pubdoc.bondex.com.cn:8087/fileking/app/";


    private static final OkHttpClient httpClient = new OkHttpClient
            .Builder()
            .connectTimeout(30000, TimeUnit.SECONDS)
            .build();


    public static Retrofit getRretrofit(String url) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(httpClient)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();
        return retrofit;
    }


    public static void donwolad(String url, final String filePath, final DownloadListener listener) {

        String httpurl = url.substring(0, url.lastIndexOf("/") + 1);
        String name = url.substring(url.lastIndexOf("/") + 1, url.length());
        Log.i("INstall", "httpurl  " + httpurl + "  \n  name " + name);
        NetApi netApi = HttpConnection.getRretrofit(httpurl).create(NetApi.class);

        netApi.downloadFile(name).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        FileUtils.writeResponseToDisk(filePath, response, listener);
                    }
                });
                thread.setDaemon(true);
                thread.start();

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    public static void checkVersion(final UpdateListener listener) {

        Observable<String> observable = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(final ObservableEmitter<String> emitter) throws Exception {

                Call<String> call = getRretrofit(VERSION_URL).create(NetApi.class).getVersion();

                call.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {


                        if (response.body() == null) {
                            emitter.onNext("N");
                        } else {
                            emitter.onNext(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {

                        emitter.onNext("N");
                        t.toString();
                    }
                });


            }
        });
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

                if (s.equals("N")) {
listener.notUpdate("N");
                } else {

                    Gson gson = new Gson();

                    UpdateBean bean = gson.fromJson(s, UpdateBean.class);

                    if(bean != null){
                        listener.update(bean);
                    }else {
                        listener.notUpdate(s);
                    }
                }

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);

    }


}
