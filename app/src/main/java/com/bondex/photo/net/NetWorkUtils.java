package com.bondex.photo.net;

import android.util.Log;

import com.bondex.library.util.CommonUtils;
import com.bondex.photo.bean.UploadResultBean;
import com.bondex.photo.utils.itf.UploadCallBack;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * date: 2020/5/19
 *
 * @Author: ysl
 * description:
 */
public class NetWorkUtils {

    private static String INTERFACE_ADDRESS = "/Api/Upload";
    private static String TEST_URL = "http://222.173.105.194:20518/Api/Upload";
    private static OkHttpClient okHttpClient;
    private static String mImageType = "multipart/form-data";


    private static OkHttpClient getHttpClient() {

        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient.Builder().build();
        }

        return okHttpClient;
    }

    private static Request getRequst(String url, MultipartBody body) {

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        return request;
    }

    public static void upload(String url, final String path, String param, final UploadCallBack callback) {

       final File file = new File(path);
       final String fileName = file.getName();
        int fileTypeIndex = fileName.lastIndexOf(".");
        String fileType = fileName.substring(fileTypeIndex+1);
//        Log.i("aaa", "fileType " + path);


        final String net_url = CommonUtils.isEmpty(param) ? url + INTERFACE_ADDRESS : url + INTERFACE_ADDRESS+"?FlowNo="+param;

        Log.i("aaa", "upload_url " + net_url+" \n param"+param);

        Observable<UploadResultBean> observable = Observable.create(new ObservableOnSubscribe<UploadResultBean>() {
            @Override
            public void subscribe(final ObservableEmitter<UploadResultBean> emitter) {

                if (!file.exists()){
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                final RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
                final MultipartBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", fileName, requestBody)
                        .addFormDataPart("imagetype", mImageType)

                        .build();
                Call call = getHttpClient().newCall(getRequst(net_url, body));
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e("aaa", "failed " + e.toString());

                        UploadResultBean bean = new UploadResultBean();
                        bean.setMessage("error");
                        emitter.onNext(bean);
                    }

                    @Override
                    public void onResponse(Call call, Response response) {

                        if (response.code() == 200) {

                            if (response.body() != null) {
//                            {"Message":"文件上传成功.","Data":"1589873300860.png","Code":0,"ExecutionTime":"2020-05-19 15:28:20","Success":true}
                                try {
                                    String msg = response.body().string();
                                    Log.e("aaa", "message " + msg);

                                    Gson gson = new Gson();
                                    UploadResultBean bean = null;

                                    try {
                                        bean = gson.fromJson(msg, UploadResultBean.class);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    if (bean == null) {
                                        bean = new UploadResultBean();
                                        bean.setMessage("error");
                                    }

                                    emitter.onNext(bean);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (response.code() == 500) {
                            UploadResultBean bean = new UploadResultBean();
                            bean.setMessage("error");
                            emitter.onNext(bean);
                        } else if (response.code() == 404) {

                            UploadResultBean bean = new UploadResultBean();
                            bean.setMessage("not");
                            emitter.onNext(bean);
                        }
                    }
                });

            }
        });
        Consumer<UploadResultBean> consumer = new Consumer<UploadResultBean>() {
            @Override
            public void accept(UploadResultBean uploadBean) throws Exception {

                if (callback == null) {
                    return;
                }

                if (uploadBean.getMessage().equals("error")) {

                    callback.uploadError("服务器错误", path);
                } else if (uploadBean.getMessage().equals("not")) {
                    callback.uploadError("服务器地址错误", path);
                } else {

                    if (uploadBean.isSuccess()) {
                        callback.uploadSuccess(uploadBean.getMessage(), path);

                    } else {
                        callback.uploadError(uploadBean.getMessage(), path);

                    }

                }

            }
        };
        Consumer<Throwable> errorConsumer = new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

                callback.uploadError(throwable.toString(), path);

                Logger.i("httpError " + throwable.toString());
            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, errorConsumer);
    }

}
