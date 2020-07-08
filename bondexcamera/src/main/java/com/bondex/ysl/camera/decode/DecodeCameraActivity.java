package com.bondex.ysl.camera.decode;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bondex.library.app.PhotoApplication;
import com.bondex.library.util.NoDoubleClickListener;
import com.bondex.library.util.StatusBarUtil;
import com.bondex.ysl.camera.CameraActivity;
import com.bondex.ysl.camera.ISCameraConfig;
import com.bondex.ysl.camera.R;
import com.bondex.ysl.camera.bean.ImgBean;
import com.bondex.ysl.camera.compross.Luban;
import com.bondex.ysl.camera.ui.CameraSingleton;
import com.bondex.ysl.camera.ui.CameraView;
import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.inter.DecodeCameraImgCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * date: 2020/6/10
 *
 * @Author: ysl
 * description:
 */
public class DecodeCameraActivity extends AppCompatActivity {

    public static void startActivityForResult(Activity activity, ISCameraConfig config, int requestCode) {

        Intent intent = new Intent(activity, DecodeCameraActivity.class);

        intent.putExtra(LIST_KEY, config);
        activity.startActivityForResult(intent, requestCode);
    }

    private static final String LIST_KEY = "config";
    private CameraView cameraView;
    private boolean granted = false;

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageView ivTakePituer, ivCancel, ivBack, ivAuto, ivRotation;
    private ImageView iv_picture, ivImg;
    private TextView tvAuto, tvTitle;

    private RelativeLayout rlTakePhoto, rlTakePictureBottom;
    private LinearLayout llChoosePicture;
    private TextView tvCancel, tvFinish;

    /**
     * 拍照的图片的数量
     */
    private ArrayList<ImgBean> takeImgs = new ArrayList<>();

    private ImgBean currentImgBean;


    public static final String FINISH_KEY = "result";
    private static final int TAKE_PHOTO = 0;
    private static final int FINISH = 1;
    private String filePath;
    private ISCameraConfig config;
    private int delayTime = 0;
    private final String TAG = DecodeCameraActivity.class.getSimpleName();
    private ThreadPoolExecutor executor;

    private NoDoubleClickListener clickListener = new NoDoubleClickListener() {
        @Override
        public void click(View v) {

            int i = v.getId();
//拍照
//取消
//完成
//取消拍照
//是否确认状态下返回
//确认拍照
//自动
//镜头切换
            if (i == R.id.takepicture) {

                if (config.takeDelay > 0) {
                    autoTake();
                } else {
                    takePicture();

                }

            } else if (i == R.id.tv_cancel_take_pic) {

                cancel();
            } else if (i == R.id.tv_finish_take_pic) {
//拍照完成，准备返回数据
                onFinish();

            } else if (i == R.id.iv_cancel) {
                delete();
                setLayoutStatus(false);
            } else if (i == R.id.iv_back) {
//                delTakePictures();
                cancel();
            } else if (i == R.id.iv_confirm) {
                setLayoutStatus(false);

            } else if (i == R.id.tv_auto || i == R.id.iv_auto) {

                final boolean isOpen = tvAuto.getText().toString().equals("关闭");

            } else if (i == R.id.iv_rotation_camera) {

            }

        }

    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        barcodeScannerView = initializeContent();

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);

        capture.setIsCamrera(true, new BarcodeCallback() {
            @Override
            public void barcodeResult(BarcodeResult result) {

                currentImgBean = new ImgBean();
                currentImgBean.setQrCode(result.getText());

                Log.i(TAG, "camera decode " + result.getText());
            }

            @Override
            public void possibleResultPoints(List<ResultPoint> resultPoints) {

            }
        });
        capture.decode();
        barcodeScannerView.setIsCamera(true);
        Log.i("aaa", " decodeCamera");

        filePath = getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DCIM).getAbsolutePath() + File.separator + "img";


        initData();
        initStatusBar();
        initView();
    }

    private void initData() {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);
        executor = new ThreadPoolExecutor(3,5,3, TimeUnit.SECONDS,workQueue);

        config = getIntent().getParcelableExtra(LIST_KEY);

        delayTime = config.takeDelay * 1000;
    }

    private void initView() {

        rlTakePhoto = findViewById(R.id.rl_take_photo);
        rlTakePictureBottom = findViewById(R.id.rl_take_picture_bottom);

        llChoosePicture = findViewById(R.id.ll_choose_picture);
        iv_picture = findViewById(R.id.iv_picture);
        ivTakePituer = findViewById(R.id.takepicture);
        ivImg = findViewById(R.id.iv_confirm);
        ivCancel = findViewById(R.id.iv_cancel);

        tvCancel = findViewById(R.id.tv_cancel_take_pic);
        tvFinish = findViewById(R.id.tv_finish_take_pic);
        ivAuto = findViewById(R.id.iv_auto);
        tvAuto = findViewById(R.id.tv_auto);


        ivAuto.setOnClickListener(clickListener);
        ivTakePituer.setOnClickListener(clickListener);
        tvCancel.setOnClickListener(clickListener);
        tvFinish.setOnClickListener(clickListener);
        ivCancel.setOnClickListener(clickListener);
        ivImg.setOnClickListener(clickListener);
        tvAuto.setOnClickListener(clickListener);
    }

    /**
     * 设置 布局的状态（拍照和是否确认）
     *
     * @param isShowConfirmPhtoGraph
     */


    public void setLayoutStatus(boolean isShowConfirmPhtoGraph) {

        //是否确认的头部
        rlTakePhoto.setVisibility(isShowConfirmPhtoGraph ? View.GONE : View.VISIBLE);
        //是否确认的底部
        llChoosePicture.setVisibility(isShowConfirmPhtoGraph ? View.VISIBLE : View.GONE);
        //拍照的底部
        rlTakePictureBottom.setVisibility(isShowConfirmPhtoGraph ? View.GONE : View.VISIBLE);
        //surfaceview
        barcodeScannerView.setVisibility(isShowConfirmPhtoGraph ? View.GONE : View.VISIBLE);

        if (!isShowConfirmPhtoGraph) {
//            CameraSingleton.getInstance().autoCameraFocus();
//            预览解析二维码图片
            capture.decode();
        }
        //图片的预览
        iv_picture.setVisibility(isShowConfirmPhtoGraph ? View.VISIBLE : View.GONE);
        ivTakePituer.setClickable(!isShowConfirmPhtoGraph);
    }


    private void autoTake() {


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (config.takeDelay > 0) {

                    for (int i = 0; i < 3; ++i) {
                        takeHandle.sendEmptyMessage(TAKE_PHOTO);
                        try {

                            if (i == 2 && config.isAutoTak) {
//         等待2s，用于保存照片
                                Thread.sleep(2000);
                                takeHandle.sendEmptyMessage(FINISH);
                            }
                            Thread.sleep(delayTime);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                } else {

                    takeHandle.sendEmptyMessage(TAKE_PHOTO);
                }


            }
        });
        thread.setDaemon(true);
        thread.start();


    }

    private void playAutdio() {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(this, notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cancel() {

        for (ImgBean imgBean : takeImgs) {

            if (imgBean != null && !TextUtils.isEmpty(imgBean.getPath())) {
                File file = new File(imgBean.getPath());
                file.delete();
            }
        }

        finish();
    }


    private void delete() {

        if (currentImgBean != null && !TextUtils.isEmpty(currentImgBean.getPath())) {
            File file = new File(currentImgBean.getPath());
            file.delete();

            if (takeImgs.contains(currentImgBean)) {
                takeImgs.remove(currentImgBean);
            }

        }


    }

    private void takePicture() {
        playAutdio();

        barcodeScannerView.takePicture(new DecodeCameraImgCallBack() {
            @Override
            public void imgData(final byte[] data) {


                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

                bitmap = rotatingImage(bitmap, 90);
                final Bitmap finalBitMap = bitmap;

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        iv_picture.setImageBitmap(finalBitMap);
                        //设置显示  是否确认的布局
                        setLayoutStatus(true);

                        final String fileName = System.currentTimeMillis() + ".png";

                        if (currentImgBean == null) {
                            currentImgBean = new ImgBean();
                        } else if (takeImgs.contains(currentImgBean)) {
                            currentImgBean = new ImgBean();
                        }


                        currentImgBean.setPath(filePath + File.separator + fileName);
                        currentImgBean.setFileName(fileName);

                        takeImgs.add(currentImgBean);

                        savePircture(data, filePath, fileName);


                        if (config.takeDelay > 0) {

                            boolean autoCancel = new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setLayoutStatus(false);
                                }
                            }, 1500);
                        }

//                        else if (config.isAutoTak) {
//                            onFinish();
//                        }

                    }
                });

            }
        });
    }

    private void savePircture(final byte[] bytes, final String path, final String fileName) {

        executor.execute(new Runnable() {
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
                    Luban.with(PhotoApplication.getContext()).ignoreBy(100).setTargetDir(file_path).setCompressRatio(config.compressRatio).load(tmpFile).get();

                    if (config.isAutoTak && config.takeDelay == 0) {
                        takeHandle.sendEmptyMessage(FINISH);

                    }
                    Log.i("aaa", "保存照片 " + currentImgBean.getPath() + "\n" + currentImgBean.getQrCode());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void onFinish() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            intent.putParcelableArrayListExtra(FINISH_KEY, takeImgs);

        }
        setResult(RESULT_OK, intent);
        finish();
    }

    private void initStatusBar() {

        StatusBarUtil.setColor(this, getResources().getColor(com.google.zxing.client.android.R.color.black_panit));
    }


    /**
     * Override to use a different layout.
     *
     * @return the DecoratedBarcodeView
     */
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.activity_decode_camera_layout);
        return (DecoratedBarcodeView) findViewById(com.google.zxing.client.android.R.id.zxing_barcode_scanner);
    }

    private Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);
        matrix.postScale(1, 1);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (config.isAutoTak) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    autoTake();
                }
            }, 800);

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.finishSelf();

        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private Handler takeHandle = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == TAKE_PHOTO) {
                takePicture();
            } else if (msg.what == FINISH) {
                onFinish();
            }


        }
    };
}
