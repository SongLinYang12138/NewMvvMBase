package com.bondex.ysl.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bondex.library.util.StatusBarUtil;
import com.bondex.library.util.ToastUtils;
import com.bondex.ysl.camera.bean.BitmapBean;
import com.bondex.ysl.camera.bean.ImgBean;
import com.bondex.ysl.camera.ui.CameraSingleton;
import com.bondex.ysl.camera.ui.CameraView;
import com.bondex.ysl.camera.ui.utils.DecodeBitmapCallback;
import com.google.zxing.Result;
import com.bondex.ysl.camera.ui.DecodeBitmapThread;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * date: 2019/7/17
 * Author: ysl
 * description:
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {


    public static void startActivityForResult(Activity activity, ISCameraConfig config, int requestCode) {

        Intent intent = new Intent(activity, CameraActivity.class);

        intent.putExtra(LIST_KEY, config);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityFroResult(Fragment fragment, ISCameraConfig config, int requestCode) {

        Intent intent = new Intent(fragment.getActivity(), CameraActivity.class);
        intent.putExtra(LIST_KEY, config);
        fragment.getActivity().startActivityForResult(intent, requestCode);
    }


    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final String LIST_KEY = "config";
    private static final int REQUEST_PERMISSION = 113;
    private CameraView cameraView;
    private boolean granted = false;
    private ISCameraConfig config;

    private ImageView iv_picture;
    private ImageView ivImg;
    private TextView tvAuto, tvTitle;
    private ListView listView;
    /**
     * 拍照的图片的数量
     */
    private ArrayList<ImgBean> takeImgs = new ArrayList<>();


    public static final String FINISH_KEY = "result";
    private static final int TAKE_PHOTO = 0;
    private static final int FINISH = 1;

    private ImageView ivTakePituer, ivCancel, ivBack, ivAuto, ivRotation;

    private RelativeLayout rlTakStatus, rlTakePhoto, rlTakePictureBottom;
    private LinearLayout llChoosePicture;
    private TextView tvCancel, tvFinish;
    private BitmapBean curentBitmap;


    private boolean startTak = false;
    private int delayTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        initData();

        rlTakStatus = findViewById(R.id.rl_take_staus);
        rlTakePhoto = findViewById(R.id.rl_take_photo);
        rlTakePictureBottom = findViewById(R.id.rl_take_picture_bottom);

        llChoosePicture = findViewById(R.id.ll_choose_picture);
        cameraView = findViewById(R.id.camera);
        iv_picture = (ImageView) findViewById(R.id.iv_picture);
        ivTakePituer = findViewById(R.id.takepicture);
        ivImg = (ImageView) findViewById(R.id.iv_confirm);
        ivCancel = findViewById(R.id.iv_cancel);

        listView = findViewById(R.id.listview);

        tvCancel = findViewById(R.id.tv_cancel_take_pic);
        tvFinish = findViewById(R.id.tv_finish_take_pic);
        ivBack = findViewById(R.id.iv_back);
        ivAuto = findViewById(R.id.iv_auto);
        ivRotation = findViewById(R.id.iv_rotation_camera);
        tvTitle = findViewById(R.id.tv_title);

        getPermissions();

        takeImgs.clear();
        tvAuto = (TextView) findViewById(R.id.tv_auto);

        ivAuto.setOnClickListener(this);
        ivTakePituer.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivRotation.setOnClickListener(this);
        ivImg.setOnClickListener(this);
        tvAuto.setOnClickListener(this);


        initStatusBar();
    }


    private void initData() {


        config = getIntent().getParcelableExtra(LIST_KEY);

        delayTime = config.takeDelay * 1000;

        CameraSingleton.getInstance().setResultHandler(parseCodeHandler);

    }

    private void initStatusBar() {

        StatusBarUtil.setColor(this, getResources().getColor(R.color.black_panit));
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            cameraView.onResume();
        } else {
            if (granted) {
                cameraView.onResume();
            }
        }
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
            }, 2000);

        }
    }


    private void autoTake() {


        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

                if (config.takeDelay > 0) {


                    for (int i = 0; i < 3; ++i) {


                        takeHandle.sendEmptyMessage(TAKE_PHOTO);
                        startTak = false;

                        try {

//                            while (!startTak) {
//                                Log.i("aaa", "循环检查" + startTak);
//                                Thread.sleep(500);
//                            }

                            if (i == 2 && config.isAutoTak) {
//         等待2s，用于保存照片
                                Thread.sleep(3000);
                                takeHandle.sendEmptyMessage(FINISH);
                            }
                            Thread.sleep(delayTime);


                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }


                    }
                } else {

                    try {
                        Thread.sleep(2000);
                        takeHandle.sendEmptyMessage(TAKE_PHOTO);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }
        });
        thread.setDaemon(true);
        thread.start();


    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraView.onPause();
        Log.i("JCameraView", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CameraSingleton.getInstance().doDestroyCamera();
        Log.i("JCameraView", "onDestroy");
        startTak = false;
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

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager
                            .PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager
                            .PERMISSION_GRANTED) {
                granted = true;
                //具有权限
            } else {
                //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(CameraActivity.this, new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA}, REQUEST_PERMISSION);
                granted = false;
            }
        }
    }

    /**
     * 拍照的点击事件
     *
     * @param v
     */

    @Override
    public void onClick(View v) {
        int i = v.getId();//拍照
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
            delTakePictures();
        } else if (i == R.id.iv_confirm) {
            setLayoutStatus(false);
            savePicture();
        } else if (i == R.id.tv_auto || i == R.id.iv_auto) {


            final boolean isOpen = tvAuto.getText().toString().equals("关闭");

            CameraSingleton.getInstance().flashOpen(isOpen, new CameraSingleton.CamOpenOverCallback() {
                @Override
                public void cameraHasOpened() {

                }

                @Override
                public void cameraSwitchSuccess() {
                    tvAuto.setText(isOpen ? "开启" : "关闭");
                }
            });


        } else if (i == R.id.iv_rotation_camera) {
            //前置后置摄像头切换
            CameraSingleton.getInstance().switchCamera(new CameraSingleton.CamOpenOverCallback() {
                @Override
                public void cameraHasOpened() {

                }

                @Override
                public void cameraSwitchSuccess() {

                }
            });
        }
    }


    /**
     * 拍照
     */
    private void takePicture() {

        playAutdio();
//      执行拍照 并且将拍照按钮设置不可点击
        ivTakePituer.setClickable(false);


        CameraSingleton.getInstance().takePicture(new CameraSingleton.TakePictureCallback() {
            @Override
            public void captureResult(Bitmap bitmap, String filePath, String fileName) {

                curentBitmap = new BitmapBean(filePath, 0, fileName, "");

                iv_picture.setImageBitmap(bitmap);
                //设置显示  是否确认的布局
                setLayoutStatus(true);

                if (config.isAutoTak || config.takeDelay > 0) {
                    savePicture();
                }


            }
        });
    }

    /**
     * 保存照片
     */
    private void savePicture() {

        if (curentBitmap == null) {
            ToastUtils.showToast("请先拍照");
            return;
        }

        if (config.takeDelay > 0) {

            boolean autoCancel = new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    setLayoutStatus(false);
                    startTak = true;
                }
            }, 1500);
        }
        ivImg.setClickable(false);
        ToastUtils.showToast("正在保存");

        ImgBean imgBean = new ImgBean(curentBitmap.getPath(), curentBitmap.getQrCode(), curentBitmap.getFileName(), 0);

        if (takeImgs.contains(imgBean)) {

            int index = takeImgs.indexOf(imgBean);
            takeImgs.set(index, imgBean);
        } else {
            takeImgs.add(imgBean);
        }

        if (config.isAutoTak && config.takeDelay == 0) {
            onFinish();
        }
        ivImg.setClickable(true);

        //        拍完后直接保存返回
//        setLayoutStatus(false);

    }

    /**
     * 设置 布局的状态（拍照和是否确认）
     *
     * @param isShowConfirmPhtoGraph
     */


    public void setLayoutStatus(boolean isShowConfirmPhtoGraph) {

        //拍照的头部
        rlTakStatus.setVisibility(isShowConfirmPhtoGraph ? View.VISIBLE : View.GONE);
        //是否确认的头部
        rlTakePhoto.setVisibility(isShowConfirmPhtoGraph ? View.GONE : View.VISIBLE);
        //是否确认的底部
        llChoosePicture.setVisibility(isShowConfirmPhtoGraph ? View.VISIBLE : View.GONE);
        //拍照的底部
        rlTakePictureBottom.setVisibility(isShowConfirmPhtoGraph ? View.GONE : View.VISIBLE);
        //surfaceview
        cameraView.setVisibility(isShowConfirmPhtoGraph ? View.GONE : View.VISIBLE);

//        if (!isShowConfirmPhtoGraph) {
//            CameraSingleton.getInstance().autoCameraFocus();
//        }

        //图片的预览
        iv_picture.setVisibility(isShowConfirmPhtoGraph ? View.VISIBLE : View.GONE);
        listView.setVisibility(isShowConfirmPhtoGraph ? View.VISIBLE : View.GONE);
        ivTakePituer.setClickable(!isShowConfirmPhtoGraph);


    }

    private void onFinish() {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            intent.putParcelableArrayListExtra(FINISH_KEY, takeImgs);

        }
        setResult(RESULT_OK, intent);
        finish();
    }


    /**
     * 删除 拍照的图片
     */
    private void delTakePictures() {
        //如果取消的图片已经保存过 则需要在链表中删除
        setLayoutStatus(false);

        cancel();
    }

    private void delete() {

        if (curentBitmap != null) {
            File file = new File(curentBitmap.getPath());
            if (file.exists()) {
                file.delete();
            }

        }

    }

    private void cancel() {

        DeleteTask deleteTask = new DeleteTask();
        deleteTask.execute();
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
    private Handler parseCodeHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            if (msg.what == 101) {

                JSONObject ob = (JSONObject) msg.obj;

                try {
                    String fileName = ob.getString("fileName");
                    String qrcode = ob.getString("qrCode");

                    if (curentBitmap != null && curentBitmap.getFileName().equals(fileName)) {
                        curentBitmap.setQrCode(qrcode);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }


        }
    };

    /*
     * 取消或返回时，删除以保存的照片信息
     * */
    private class DeleteTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {

            try {

                for (int i = 0; i < takeImgs.size(); ++i) {
                    File file = new File(takeImgs.get(i).getPath());
                    file.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


            return "Y";
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            takeImgs.clear();
            finish();

        }

    }


}
