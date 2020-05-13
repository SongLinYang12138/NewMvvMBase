package com.bondex.library.app;

import android.content.Context;
import android.content.res.Configuration;
import androidx.annotation.NonNull;
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
        init();
    }

    private void initLogger() {

        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void init() {

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }
}
