package com.bondex.ysl.camera.decode;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bondex.ysl.camera.ui.CameraSingleton;
import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.journeyapps.barcodescanner.BitmapLuminanceSource;
import com.journeyapps.barcodescanner.ConcurrentDecodeThread;
import com.journeyapps.barcodescanner.Decoder;
import com.journeyapps.barcodescanner.SourceData;
import com.journeyapps.barcodescanner.Util;
import com.journeyapps.barcodescanner.camera.PreviewCallback;
import com.orhanobut.logger.Logger;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * date: 2020/6/9
 *
 * @Author: ysl
 * description:
 */
public class DecodeThread {

    //线程池

    private ThreadPoolExecutor executor;
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5);
    private ThreadFactory factory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {

            System.out.println("factory " + r.toString());
            Thread thread = new Thread(r, "new-thread ");

            return thread;
        }
    };
    private RejectedExecutionHandler rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        }
    };


    private Decoder decoder;

    private final Object LOCK = new Object();
    private static final int DECODE_SUCCESS = 101;
    private static final int DECODE_FAILE = 100;
    private boolean running = false;

    private DecodeCallBack decodeCallBack = new DecodeCallBack() {
        @Override
        public void onPreview(Bitmap bitmap) {
            DecodeRunnable decodeRunnable = new DecodeRunnable();
            decodeRunnable.setSourceData(bitmap);

            if (executor.getPoolSize() < executor.getMaximumPoolSize()) {
                executor.execute(decodeRunnable);
            } else if (workQueue.size() < 5) {
                workQueue.add(decodeRunnable);
            }
        }

        @Override
        public void onPreviewError(Exception e) {
            synchronized (LOCK) {
                if (running) {
                    // Post to our thread.
                    Logger.i("priviewFailed");
                }
            }
        }
    };

    private PreviewCallback previewCallback = new PreviewCallback() {
        @Override
        public void onPreview(SourceData sourceData) {

            DecodeRunnable decodeRunnable = new DecodeRunnable();
//            decodeRunnable.setSourceData(sourceData);

            if (executor.getPoolSize() < executor.getMaximumPoolSize()) {
                executor.execute(decodeRunnable);
            } else if (workQueue.size() < 5) {
                workQueue.add(decodeRunnable);
            }
        }

        @Override
        public void onPreviewError(Exception e) {

            synchronized (LOCK) {
                if (running) {
                    // Post to our thread.
                    Logger.i("priviewFailed");
                }
            }
        }
    };

    private Handler resultHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == DECODE_SUCCESS) {
                stop();
            } else if (msg.what == DECODE_FAILE) {
                requestNextPreview();
            }

        }
    };

    public DecodeThread(Decoder decoder) {
        this.decoder = decoder;
        executor = new ThreadPoolExecutor(3, 5, 3000, TimeUnit.SECONDS, workQueue);
    }

    public void start() {

        running = true;
        requestNextPreview();
    }


    private void requestNextPreview() {

        CameraSingleton.getInstance().requestNextPreivew(decodeCallBack);

    }

    /**
     * Stop decoding.
     * <p>
     * This must be called from the UI thread.
     */
    public void stop() {
        Util.validateMainThread();

        synchronized (LOCK) {
            running = false;
            executor.shutdownNow();

        }
    }


    protected LuminanceSource createSource(SourceData sourceData) {
//        if (this.cropRect == null) {
//            return null;
//        } else {
        return sourceData.createSource();
//        }
    }

    private void decode(Bitmap sourceData) {
        long start = System.currentTimeMillis();
        Result rawResult = null;
//        sourceData.setCropRect(cropRect);
//        LuminanceSource source = createSource(sourceData);
        BitmapLuminanceSource source = new BitmapLuminanceSource(sourceData);

        if (source != null) {
            rawResult = decoder.decode(source);
        }

        if (rawResult != null) {
            Logger.i("解析成功  " + rawResult.getText());

            Message msg = Message.obtain(resultHandler, DECODE_SUCCESS, rawResult);
            msg.sendToTarget();
        } else {
            Logger.i("解析失败");

            Message msg = Message.obtain(resultHandler, DECODE_FAILE);
            msg.sendToTarget();
            requestNextPreview();
        }


        Log.e("aaa camera", rawResult == null ? "解析失败" : rawResult.getText());
    }

    private class DecodeRunnable implements Runnable {

//        private SourceData sourceData;

//        public void setSourceData(SourceData sourceData) {
//            this.sourceData = sourceData;
//        }

        private Bitmap sourceData;

        public void setSourceData(Bitmap sourceData) {
            this.sourceData = sourceData;
        }

        @Override
        public void run() {

            decode(sourceData);
        }
    }

}
