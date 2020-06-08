package com.bondex.library.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.SyncStateContract;

import androidx.annotation.NonNull;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * date: 2020/4/24
 *
 * @Author: ysl
 * description:
 */
public class PhotoApplication extends BaseApplication implements Thread.UncaughtExceptionHandler {

    private static Context context;


    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();

        init();
    }

    public static Context getContext() {
        return context;
    }

    private void initLogger() {

        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    private void init() {

        initLogger();
        initImageLoader();

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    private void initImageLoader() {

        String file_path = getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "cache";
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //设置下载的图片是否缓存在内存中
                .cacheInMemory(false)
                //设置下载的图片是否缓存在SD卡中
                .cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .considerExifParams(true)
                // 设置图片在下载前是否重置，复位
                .resetViewBeforeLoading(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                // max width, max height，即保存的每个缓存文件的最大长宽
                //线程池内加载的数量
                .memoryCacheExtraOptions(480, 800).threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                //将保存的时候的URI名称用MD5 加密
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现

                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024))
// 内存缓存的最大值
                .memoryCacheSize(2 * 1024 * 1024)
                // 50 Mb sd卡(本地)缓存的最大值
                .diskCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                // 由原先的discCache -> diskCache
                .defaultDisplayImageOptions(options)
                //自定义缓存路径
                .diskCache(new UnlimitedDiskCache(new File(file_path)))
                // connectTimeout (5 s), readTimeout (30 s)超时时间

                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                // Remove for release app
                .writeDebugLogs()
                .build();
        //全局初始化此配置
        ImageLoader.getInstance().init(config);
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        String result = getStackTrace(e);
        Logger.i(" 异常 " + result);
//        Intent intent = new Intent(this, LoginActivity.class);
//        startActivity(intent);
    }

    private String getStackTrace(Throwable th) {

        final Writer result = new StringWriter();

        final PrintWriter printWriter = new PrintWriter(result);

        // If the exception was thrown in a background thread inside

        // AsyncTask, then the actual exception can be found with getCause

        Throwable cause = th;

        while (cause != null) {

            cause.printStackTrace(printWriter);

            cause = cause.getCause();

        }
        final String stacktraceAsString = result.toString();

        printWriter.close();

        return stacktraceAsString;
    }
}
