package com.bondex.ysl.camera.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.*;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import com.bondex.library.app.PhotoApplication;
import com.bondex.ysl.camera.bean.BitmapBean;
import com.bondex.ysl.camera.bean.ImgBean;
import com.bondex.ysl.camera.compross.Luban;
import com.bondex.ysl.camera.decode.DecodeCallBack;
import com.bondex.ysl.camera.decode.DecodeThread;
import com.bondex.ysl.camera.ui.utils.AngleUtil;
import com.bondex.ysl.camera.ui.utils.CameraParamUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.journeyapps.barcodescanner.BitmapLuminanceSource;
import com.journeyapps.barcodescanner.Decoder;
import com.journeyapps.barcodescanner.DecoderFactory;
import com.journeyapps.barcodescanner.DecoderResultPointCallback;
import com.journeyapps.barcodescanner.DefaultDecoderFactory;
import com.journeyapps.barcodescanner.Size;
import com.journeyapps.barcodescanner.SourceData;
import com.journeyapps.barcodescanner.camera.PreviewCallback;
import com.journeyapps.barcodescanner.inter.DecodeImgCallback;
import com.journeyapps.barcodescanner.utils.DecodeImgThread;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 相机的单例
 *
 * @autor timi
 * create at 2017/6/2 10:19
 */

public class CameraSingleton {

    private final String TAG = CameraSingleton.class.getSimpleName();

    private Camera mCamera;
    private Camera.Parameters mParams;
    //是否在预览
    private boolean isPreviewing = false;
    //单例
    private static CameraSingleton instance;
    private boolean isOpenflash;
    private CameraPrivewCallBack privewCallBack = null;


    private int SELECTED_CAMERA = -1;
    private int CAMERA_POST_POSITION = -1;
    private int CAMERA_FRONT_POSITION = -1;

    private SurfaceHolder mHolder = null;
    private float screenProp = -1.0f;
    //预览的宽高
    private int preview_width;
    private int preview_height;
    //角度 旋转
    private int angle = 0;
    private int rotation = 0;

    // Actual chosen preview size
    private Size requestedPreviewSize;
    private Size previewSize;

    private String filePath;
    private Handler resultHandler;
    private DecoderFactory decoderFactory;

    final int nowAngle = (angle + 90) % 360;



    public void setResultHandler(Handler resultHandler) {
        this.resultHandler = resultHandler;
    }

    //构造方法
    private CameraSingleton() {
        findAvailableCameras();
        SELECTED_CAMERA = CAMERA_POST_POSITION;
        saveExecutors = new ThreadPoolExecutor(3, 5, 3, TimeUnit.SECONDS, workQueue);

        filePath = PhotoApplication.getContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "img";

        privewCallBack = new CameraPrivewCallBack();


    }

    //获取实例
    public static synchronized CameraSingleton getInstance() {
        if (instance == null) {
            instance = new CameraSingleton();
        }
        return instance;
    }

    private ThreadPoolExecutor saveExecutors;
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);

    /**
     * 获取角度
     */
    private SensorManager sm = null;
    private SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (Sensor.TYPE_ACCELEROMETER != event.sensor.getType()) {
                return;
            }
            float[] values = event.values;
            angle = AngleUtil.getSensorAngle(values[0], values[1]);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

    /**
     * 找到可用的摄像头
     */
    private void findAvailableCameras() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        int cameraNum = Camera.getNumberOfCameras();
        for (int i = 0; i < cameraNum; i++) {
            Camera.getCameraInfo(i, info);
            switch (info.facing) {
                case Camera.CameraInfo.CAMERA_FACING_FRONT:
                    CAMERA_FRONT_POSITION = info.facing;
                    break;
                case Camera.CameraInfo.CAMERA_FACING_BACK:
                    CAMERA_POST_POSITION = info.facing;
                    break;
            }
        }
    }

    /**
     * 打开摄像头
     *
     * @param callback
     */
    void doOpenCamera(CamOpenOverCallback callback) {
        if (mCamera == null) {
            openCamera(SELECTED_CAMERA);
        }
        callback.cameraHasOpened();
    }

    /**
     * 打开摄像头（前置  后置）
     *
     * @param id
     */
    private void openCamera(int id) {
        mCamera = Camera.open(id);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mCamera.enableShutterSound(false);
        }


        Camera.Size realPreviewSize = mCamera.getParameters().getPreviewSize();
        previewSize = new Size(realPreviewSize.width, realPreviewSize.height);
        privewCallBack.setResolution(previewSize);

        mCamera.setOneShotPreviewCallback(privewCallBack);
    }

    public void startDecodeThread() {



    }

    /**
     * 前置 后置摄像头切换
     *
     * @param callback
     */
    public synchronized void switchCamera(CamOpenOverCallback callback) {
        if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
            SELECTED_CAMERA = CAMERA_FRONT_POSITION;
        } else {
            SELECTED_CAMERA = CAMERA_POST_POSITION;
        }
        doStopCamera();
        mCamera = Camera.open(SELECTED_CAMERA);
        doStartPreview(mHolder, screenProp, false);
        callback.cameraSwitchSuccess();
    }

    public void startPreView() {
        if (mHolder == null) {
            return;
        }

        doStartPreview(mHolder, screenProp, isOpenflash);


    }

    /**
     * 开启闪光
     *
     * @param callback
     */
    public synchronized void flashOpen(boolean isOpen, CamOpenOverCallback callback) {


        isOpenflash = isOpen;
        doStartPreview(mHolder, screenProp, isOpen);
        callback.cameraSwitchSuccess();
    }

    /**
     * doStartPreview
     */
    void doStartPreview(SurfaceHolder holder, float screenProp, boolean isFlash) {
        if (this.screenProp < 0) {
            this.screenProp = screenProp;
        }
        if (holder == null) {
            return;
        }
        this.mHolder = holder;
        if (mCamera != null) {
            try {
                mParams = mCamera.getParameters();
                Camera.Size previewSize = CameraParamUtil.getInstance().getPreviewSize(mParams
                        .getSupportedPreviewSizes(), 1000, screenProp);
//                Camera.Size pictureSize = CameraParamUtil.getInstance().getPictureSize(mParams
//                        .getSupportedPictureSizes(), 1200, screenProp);

                mParams.setPreviewSize(previewSize.width, previewSize.height);

//                preview_width = previewSize.width;
//                preview_height = previewSize.height;


                //配置如下：
                // 获取相机参数集
                // 获取支持预览照片的尺寸
//                List<Camera.Size> SupportedPreviewSizes =
//                        mParams.getSupportedPreviewSizes();
//                // 从List取出Size
//                Camera.Size previewSize = SupportedPreviewSizes.get(0);
//                mParams.setPreviewSize(previewSize.width, previewSize.height);
                //  设置预览照片的大小
                // 获取支持保存图片的尺寸
                List<Camera.Size> supportedPictureSizes =
                        mParams.getSupportedPictureSizes();
                Camera.Size pictureSize = supportedPictureSizes.get(0);

                mParams.setPictureSize(pictureSize.width, pictureSize.height);


                mParams.setFlashMode(isFlash ? Camera.Parameters.FLASH_MODE_ON : Camera.Parameters.FLASH_MODE_OFF);

                if (CameraParamUtil.getInstance().isSupportedFocusMode(mParams.getSupportedFocusModes(), Camera
                        .Parameters.FOCUS_MODE_AUTO)) {
                    mParams.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                    List<String> focusModes = mParams.getSupportedFocusModes();
                    if (focusModes != null) {
                        for (String mode : focusModes) {
                            mode.contains("continuous-video");
                            mParams.setFocusMode("continuous-video");

                        }
                    }
                }
                if (CameraParamUtil.getInstance().isSupportedPictureFormats(mParams.getSupportedPictureFormats(),
                        ImageFormat.JPEG)) {
                    mParams.setPictureFormat(ImageFormat.JPEG);
                    mParams.setJpegQuality(100);
                }


                mCamera.setParameters(mParams);
                mParams = mCamera.getParameters();
                //SurfaceView
                mCamera.setPreviewDisplay(holder);
                mCamera.setDisplayOrientation(90);
                mCamera.startPreview();
                isPreviewing = true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                mCamera.stopPreview();
            }
        }
//        startDecodeThread();
    }

    public void requestNextPreivew(DecodeCallBack callback) {
        Camera threCamera = mCamera;

        Log.i("aaa", "requestPreview " + isPreviewing);
        if (threCamera != null && isPreviewing) {

            privewCallBack.setCallback(callback);
            threCamera.setOneShotPreviewCallback(privewCallBack);
        }
    }

    /**
     * 停止预览，释放Camera
     */
    public void doStopCamera() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewDisplay(null);
                mCamera.stopPreview();
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void doDestroyCamera() {
        if (null != mCamera) {
            try {
                mCamera.setPreviewDisplay(null);
                mCamera.stopPreview();
                mHolder = null;
                isPreviewing = false;
                mCamera.release();
                mCamera = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 拍照
     */

    public void takePicture(final TakePictureCallback callback) {



        if (!isPreviewing) { //如果没有预览，就不能调用拍照功能;
            return;
        }


        mCamera.takePicture(null, null, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

                startPreView();
                final String fileName = System.currentTimeMillis() + ".png";
                final String file_path = filePath + File.separator + fileName;

                savePircture(data, filePath, fileName);


                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                Matrix matrix = new Matrix();
                if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                    matrix.setRotate(nowAngle);
                } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                    matrix.setRotate(270);
                    matrix.postScale(-1, 1);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                if (callback != null) {

                    callback.captureResult(bitmap, file_path, fileName);

                }
            }


        });
    }

    private void savePircture(final byte[] bytes, final String path, final String fileName) {


        saveExecutors.execute(new Runnable() {
            @Override
            public void run() {

                try {

                    File file = new File(path);

                    if (!file.exists()) {
                        file.mkdirs();
                    }

                    final String file_path = path + File.separator + fileName;
                    final File tmpFile = new File(file_path);

                    FileOutputStream fos = new FileOutputStream(tmpFile);
                    fos.write(bytes, 0, bytes.length);
                    fos.flush();
                    fos.close();

                    new DecodeImgThread(file_path, new DecodeImgCallback() {
                        @Override
                        public void onImageDecodeSuccess(Result result) {

                            Log.i("aaa", " decode success " + result.getText());

                            if (resultHandler != null)


                            Luban.with(PhotoApplication.getContext()).ignoreBy(100).setTargetDir(file_path).setCompressRatio(100).load(tmpFile).get();

                        }

                        @Override
                        public void onImageDecodeFailed() {
                            Log.i("aaa", "decode faile ");
                            Luban.with(PhotoApplication.getContext()).ignoreBy(100).setTargetDir(file_path).setCompressRatio(100).load(tmpFile).get();

                        }
                    }).run();


                    Log.i("aaa", "照片路径 " + file.getAbsolutePath() + "  " + file.exists());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * 自动对焦
     */
    public void autoCameraFocus() {
        if (null != mCamera) {
            //实现自动对焦
            mCamera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    if (success) {
                        camera.cancelAutoFocus();//只有加上了这一句，才会自动对焦。
                    }
                }
            });
        }
    }

    public int getCameraRotation() {
        return angle;
    }


    /**
     * 注册传感器管理器
     *
     * @param context
     */
    public void registerSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.registerListener(sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager
                .SENSOR_DELAY_NORMAL);
    }

    /**
     * 注册
     *
     * @param context
     */
    public void unregisterSensorManager(Context context) {
        if (sm == null) {
            sm = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        }
        sm.unregisterListener(sensorEventListener);
    }


    private Decoder createDecoder() {
        if (decoderFactory == null) {
            decoderFactory = createDefaultDecoderFactory();
        }
        DecoderResultPointCallback callback = new DecoderResultPointCallback();
        Map<DecodeHintType, Object> hints = new HashMap<>();
        hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, callback);
        Decoder decoder = this.decoderFactory.createDecoder(hints);
        callback.setDecoder(decoder);
        return decoder;
    }

    protected DecoderFactory createDefaultDecoderFactory() {
        return new DefaultDecoderFactory();
    }

    /**
     * 打开摄像头的回调接口
     */
    public interface CamOpenOverCallback {
        void cameraHasOpened();

        void cameraSwitchSuccess();

    }

    /**
     * 拍照回调的接口
     */
    public interface TakePictureCallback {
        void captureResult(Bitmap bitmap, String filePath, String fileName);
    }

    private class CameraPrivewCallBack implements Camera.PreviewCallback {
        private Size resolution;
        private DecodeCallBack callback;

        public void setCallback(DecodeCallBack callback) {
            this.callback = callback;
        }

        public void setResolution(Size resolution) {
            this.resolution = resolution;
        }


        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {


            Size cameraResolution = resolution;

            DecodeCallBack callback = this.callback;
            if (cameraResolution != null && callback != null) {
                try {
                    if (data == null) {
                        throw new NullPointerException("No preview data received");
                    }
//                    int format = camera.getParameters().getPreviewFormat();
//                    SourceData source = new SourceData(data, (int) (cameraResolution.width * 0.75), (int) (cameraResolution.height * 0.75), format, getCameraRotation());
//                    callback.onPreview(source);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                    Matrix matrix = new Matrix();
                    if (SELECTED_CAMERA == CAMERA_POST_POSITION) {
                        matrix.setRotate(nowAngle);
                    } else if (SELECTED_CAMERA == CAMERA_FRONT_POSITION) {
                        matrix.setRotate(270);
                        matrix.postScale(-1, 1);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    callback.onPreview(bitmap);

                } catch (RuntimeException e) {
                    // Could be:
                    // java.lang.RuntimeException: getParameters failed (empty parameters)
                    // IllegalArgumentException: Image data does not match the resolution
                    Log.e(TAG, "Camera preview failed", e);
                    callback.onPreviewError(e);
                }
            } else {
                Log.d(TAG, "Got preview callback, but no handler or resolution available");
                if (callback != null) {
                    // Should generally not happen
                    callback.onPreviewError(new Exception("No resolution available"));
                }
            }

        }
    }
}
