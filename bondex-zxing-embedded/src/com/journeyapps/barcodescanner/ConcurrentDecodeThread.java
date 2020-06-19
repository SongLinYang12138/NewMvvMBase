package com.journeyapps.barcodescanner;

import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.google.zxing.LuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.client.android.R;
import com.journeyapps.barcodescanner.camera.CameraInstance;
import com.journeyapps.barcodescanner.camera.PreviewCallback;
import com.journeyapps.barcodescanner.inter.DecodeCameraImgCallBack;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * date: 2020/5/12
 *
 * @Author: ysl
 * description:并发处理解码速度
 */
public class ConcurrentDecodeThread {
    private static final String TAG = ConcurrentDecodeThread.class.getSimpleName();

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


    private CameraInstance cameraInstance;
    private HandlerThread thread;
    private Handler handler;
    private Decoder decoder;
    private Handler resultHandler;
    private Rect cropRect;
    private boolean running = false;
    private final Object LOCK = new Object();
    private boolean isCamera;

    private final Handler.Callback callback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
//            这一步由线程池去做并发处理
//            if (message.what == R.id.zxing_decode) {
//                decode((SourceData) message.obj);
//            } else
//
            if (message.what == R.id.zxing_preview_failed) {
                // Error already logged. Try again.
                requestNextPreview();
            }
            return true;
        }
    };

    public ConcurrentDecodeThread(CameraInstance cameraInstance, Decoder decoder, Handler resultHandler) {
        Util.validateMainThread();

        this.cameraInstance = cameraInstance;
        this.decoder = decoder;
        this.resultHandler = resultHandler;

        executor = new ThreadPoolExecutor(3, 5, 3, TimeUnit.SECONDS, workQueue, factory, rejectHandler);
    }

    public Decoder getDecoder() {
        return decoder;
    }

    public void setDecoder(Decoder decoder) {
        this.decoder = decoder;
    }

    public Rect getCropRect() {
        return cropRect;
    }

    public void setCropRect(Rect cropRect) {
        this.cropRect = cropRect;
    }

    /**
     * Start decoding.
     * <p>
     * This must be called from the UI thread.
     */
    public void start() {
        Util.validateMainThread();

        thread = new HandlerThread(TAG);
        thread.start();
        handler = new Handler(thread.getLooper(), callback);
        running = true;
        requestNextPreview();
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
            handler.removeCallbacksAndMessages(null);
            thread.quit();
        }
    }

    private final PreviewCallback previewCallback = new PreviewCallback() {
        @Override
        public void onPreview(SourceData sourceData) {
            // Only post if running, to prevent a warning like this:
            //   java.lang.RuntimeException: Handler (android.os.Handler) sending message to a Handler on a dead thread

            // synchronize to handle cases where this is called concurrently with stop()
//            synchronized (LOCK) {
//                if (running) {
//                    // Post to our thread.
//                    handler.obtainMessage(R.id.zxing_decode, sourceData).sendToTarget();
//                }
//            }

            DecodeRunnable decodeRunnable = new DecodeRunnable();
            decodeRunnable.setSourceData(sourceData);

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
                    handler.obtainMessage(R.id.zxing_preview_failed).sendToTarget();
                }
            }
        }
    };

    private void requestNextPreview() {
        cameraInstance.requestPreview(previewCallback);
    }

    protected LuminanceSource createSource(SourceData sourceData) {
        if (this.cropRect == null) {
            return null;
        } else {
            return sourceData.createSource();
        }
    }

    private void decode(SourceData sourceData) {
        long start = System.currentTimeMillis();
        Result rawResult = null;
        sourceData.setCropRect(cropRect);
        LuminanceSource source = createSource(sourceData);

        if (source != null) {
            rawResult = decoder.decode(source);
        }

        Log.e("aaa", rawResult == null ? "解析失败" : rawResult.getText());

        if (rawResult != null) {
            // Don't log the barcode contents for security.
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode in " + (end - start) + " ms");
            if (resultHandler != null) {
                BarcodeResult barcodeResult = new BarcodeResult(rawResult, sourceData);
                Message message = Message.obtain(resultHandler, R.id.zxing_decode_succeeded, barcodeResult);
                Bundle bundle = new Bundle();
                message.setData(bundle);
                message.sendToTarget();
            }
        } else {
            if (resultHandler != null) {
                Message message = Message.obtain(resultHandler, R.id.zxing_decode_failed);
                message.sendToTarget();
            }
        }
        if (resultHandler != null) {
            List<ResultPoint> resultPoints = decoder.getPossibleResultPoints();
            Message message = Message.obtain(resultHandler, R.id.zxing_possible_result_points, resultPoints);
            message.sendToTarget();
        }
        requestNextPreview();
    }

    private class DecodeRunnable implements Runnable {

        private SourceData sourceData;

        public void setSourceData(SourceData sourceData) {
            this.sourceData = sourceData;
        }

        @Override
        public void run() {

            decode(sourceData);
        }
    }
}
