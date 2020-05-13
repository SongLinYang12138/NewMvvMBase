package com.bondex.ysl.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bondex.library.util.StatusBarUtil;
import com.bondex.library.util.ToastUtils;
import com.bondex.ysl.camera.compross.Luban;
import com.bondex.ysl.camera.ui.CameraSingleton;
import com.bondex.ysl.camera.ui.CameraView;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * date: 2019/7/17
 * Author: ysl
 * description:
 */
public class CameraActivity extends AppCompatActivity implements View.OnClickListener {


    public static void startActivityForResult(Activity activity, ISCameraConfig config, int requestCode) {

        Intent intent = new Intent(activity, CameraActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void startActivityFroResult(Fragment fragment, ISCameraConfig config, int requestCode) {

        Intent intent = new Intent(fragment.getActivity(), CameraActivity.class);
        fragment.getActivity().startActivityForResult(intent, requestCode);
    }


    private static final String TAG = CameraActivity.class.getSimpleName();
    private static final String LIST_KEY = "config";
    private static final int REQUEST_PERMISSION = 113;
    private CameraView cameraView;
    private boolean granted = false;

    private ImageView iv_picture;
    private ImageView ivImg;
    private TextView tvAuto, tvTitle;
    private ListView listView;
    /**
     * 拍照的图片的数量
     */
    private ArrayList<String> takeImgs = new ArrayList<>();


    private String file_path = null;
    private ThreadPoolExecutor saveExecutors;
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(2);
    public static final String FINISH_KEY = "result";
    private ImageView ivTakePituer, ivCancel, ivBack, ivAuto, ivRotation;

    private RelativeLayout rlTakStatus, rlTakePhoto, rlTakePictureBottom;
    private LinearLayout llChoosePicture;
    private TextView tvCancel, tvFinish;

    Handler finishHandler = new Handler() {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            ivImg.setClickable(true);
            onFinish();
        }
    };


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
        saveExecutors = new ThreadPoolExecutor(3, 5, 3, TimeUnit.SECONDS, workQueue);

        ivAuto.setOnClickListener(this);
        ivTakePituer.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        tvFinish.setOnClickListener(this);
        ivCancel.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivRotation.setOnClickListener(this);
        ivImg.setOnClickListener(this);
        tvAuto.setOnClickListener(this);


        file_path = getBaseContext().getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + "img";
        Log.i("aaa", " filepath " + file_path);

        initStatusBar();
    }


    private void initData() {


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

//                autoTake();

    }

    public void autoTake() {

        boolean take = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                takePicture();
                atuoSave();
            }
        }, 1000);

    }

    public void atuoSave() {

        boolean save = new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                savePicture();
            }
        }, 1000);

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


            takePicture();
        } else if (i == R.id.tv_cancel_take_pic) {

            cancel();

        } else if (i == R.id.tv_finish_take_pic) {
//拍照完成，准备返回数据
            takePicture();
        } else if (i == R.id.iv_cancel) {
            setLayoutStatus(false);
        } else if (i == R.id.iv_back) {
            delTakePictures();
        } else if (i == R.id.iv_confirm) {
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
        Log.i("aaa", "拍照");
        /**
         * 执行拍照 并且将拍照按钮设置不可点击
         */
        ivTakePituer.setClickable(false);
        CameraSingleton.getInstance().takePicture(new CameraSingleton.TakePictureCallback() {
            @Override
            public void captureResult(Bitmap bitmap) {
                iv_picture.setImageBitmap(bitmap);
                //设置显示  是否确认的布局
                setLayoutStatus(true);
            }
        });
    }

    /**
     * 保存照片
     */
    private void savePicture() {
        iv_picture.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(iv_picture.getDrawingCache());

        iv_picture.setDrawingCacheEnabled(false);

        if (bitmap != null) {

            //设置标题的文本
//                tvTitle.setText((takeImgs.size() + 1) + "/" + MaxTakePictureNum);
            saveBitmap(bitmap);

        }
        ivImg.setClickable(false);
        ToastUtils.showToast("正在保存");
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

        //图片的预览
        iv_picture.setVisibility(isShowConfirmPhtoGraph ? View.VISIBLE : View.GONE);
        listView.setVisibility(isShowConfirmPhtoGraph ? View.VISIBLE : View.GONE);
        ivTakePituer.setClickable(!isShowConfirmPhtoGraph);
//                判断改照片属于哪一个分单号


    }

    private void onFinish() {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            intent.putStringArrayListExtra(FINISH_KEY, takeImgs);
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

    public void saveBitmap(final Bitmap bitmap) {


        final Bitmap mBitmap = bitmap;


        saveExecutors.execute(new Runnable() {
            @Override
            public void run() {


                String fileName = System.currentTimeMillis() + ".png";
//                String comprossNam = System.currentTimeMillis() + "_" + hawbBean.getHawb() + ".png";
//                comprossNam = comprossNam.replaceAll(" ", comprossNam);
                String path = file_path + File.separator;

                File file = new File(file_path);

                if (!file.exists()) {
                    file.mkdirs();
                }


                File f = new File(path + fileName);
                try {
                    f.createNewFile();
                    Log.e(TAG, "创建照片" + f.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                FileOutputStream fOut = null;
                try {
                    fOut = new FileOutputStream(f);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                if (fOut == null) {
                    return;
                }


                try {
                    takeImgs.add(f.getAbsolutePath());
                    fOut.flush();
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                调用鲁班压缩，
//                Luban.with(CameraActivity.this).ignoreBy(100).setTargetDir(f.getAbsolutePath()).load(f).get();
                finishHandler.sendEmptyMessage(1);
                Log.i("aaa", "照片路径 " + f.getAbsolutePath());


            }
        });

    }

    private void cancel() {

        DeleteTask deleteTask = new DeleteTask();
        deleteTask.execute();
    }

    //压缩宽高
    private int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    /*
     *旋转图片角度
     * */
    private Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    /*
     * 取消或返回时，删除以保存的照片信息
     * */
    private class DeleteTask extends AsyncTask {
        @Override
        protected Object doInBackground(Object[] objects) {


            try {

                for (int i = 0; i < takeImgs.size(); ++i) {
                    File file = new File(takeImgs.get(i).toString());
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
