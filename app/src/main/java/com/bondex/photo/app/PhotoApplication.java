package com.bondex.photo.app;

import android.content.Context;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

/**
 * date: 2020/4/24
 *
 * @Author: ysl
 * description:
 */
public class PhotoApplication extends BaseApplication {

    public static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        initLogger();
    }

    private void initLogger() {

        Logger.addLogAdapter(new AndroidLogAdapter());
    }
}
