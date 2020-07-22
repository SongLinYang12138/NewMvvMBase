package com.bondex.photo.main;

import com.bondex.library.app.PhotoApplication;
import com.bondex.library.base.BaseModel;
import com.bondex.library.util.CommonUtils;
import com.bondex.ysl.installlibrary.InstallApk;
import com.bondex.ysl.installlibrary.download.DownloadListener;
import com.bondex.ysl.installlibrary.download.HttpConnection;
import com.bondex.ysl.installlibrary.download.UpdateBean;
import com.bondex.ysl.installlibrary.download.UpdateListener;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * date: 2020/6/1
 *
 * @Author: ysl
 * description:
 */
public class MainModel extends BaseModel<MainCallBack> {


    public MainModel(MainCallBack resultBack) {
        super(resultBack);
    }


    protected void checkVersion(){


    }

    public void download(final String url, final String path, final DownloadListener listener) {

        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {

                HttpConnection.donwolad(url, path, listener);
            }
        });
        Consumer<String> consumer = new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {

            }
        };
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer);


    }
}
