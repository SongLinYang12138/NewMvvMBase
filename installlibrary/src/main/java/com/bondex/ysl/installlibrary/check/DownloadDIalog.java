package com.bondex.ysl.installlibrary.check;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bondex.ysl.installlibrary.InstallApk;
import com.bondex.ysl.installlibrary.R;
import com.bondex.ysl.installlibrary.download.DownloadListener;
import com.bondex.ysl.installlibrary.download.HttpConnection;
import com.bondex.ysl.installlibrary.download.UpdateBean;
import com.bondex.ysl.installlibrary.ui.ProgressView;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * date: 2020/7/22
 *
 * @author: ysl
 * description:
 */
public class DownloadDIalog implements DownloadListener {

    private TextView updateTv = null;
    private ProgressView updateProgress = null;
    private Button updateConfirm = null;
    private Dialog updateDialog = null;
    protected String filePath = null;
    private Context context;

    public DownloadDIalog(Context context) {
        this.context = context;
    }

    public void showUpdateDialog(final UpdateBean updateBean) {

        if (updateDialog == null) {
            updateDialog = new Dialog(context, R.style.dialog);
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.dialog_update_layout, null);
            updateDialog.setContentView(view);
            ImageView baseBack =
                    view.findViewById(R.id.base_back);
            updateConfirm = view.findViewById(R.id.update_confirm);
            Button updateCancel = view.findViewById(R.id.update_cancel);
            updateTv = view.findViewById(R.id.update_tv);
            updateProgress = view.findViewById(R.id.update_progress);
            baseBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDialog.dismiss();

                }
            });
            updateConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateConfirm.setClickable(false);
                download(updateBean.getDownload_url(), filePath);

                }
            });
            updateCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateDialog.dismiss();

                }
            });
            updateTv.setText(updateBean.getDescription());
            WindowManager.LayoutParams lp =
                    updateDialog.getWindow().getAttributes();
            lp.width = getScreenW(context) - 20;
            int height = getScreenH(context);
            lp.height = height - height / 4;
            lp.gravity = Gravity.CENTER;
            updateDialog.getWindow().setAttributes(lp);
            updateDialog.setCanceledOnTouchOutside(true);
        }
        updateDialog.show();
    }

    public int getScreenW(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;

        return width;


    }

    public int getScreenH(Context context) {


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;

        return height;
    }

    public void download(final String url, final String path) {

        Observable observable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(ObservableEmitter emitter) throws Exception {

                HttpConnection.donwolad(url, path, DownloadDIalog.this);
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

    private int START = 1001;
    private int FINISH = 1100;
    private int FAILED = 1110;
    private Handler progressHandler = new Handler() {

        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what != START && msg.what != FINISH && msg.what != FAILED) {
                updateProgress.setCurrentValue(msg.what);
            } else {
                if (msg.what == START) {
                    if (updateTv != null) {
                        updateTv.setVisibility(View.GONE);
                        updateProgress.setVisibility(View.VISIBLE);
                    }
                } else if (msg.what == FINISH) {
                    if (updateTv != null) {
                        updateDialog.dismiss();
//                            ToastUtils.showToast("下载成功")
                        updateTv.setVisibility(View.VISIBLE);
                        updateProgress.setVisibility(View.GONE);
                        updateConfirm.setClickable(true);
                        InstallApk.install(filePath, context);
                    }
                } else if (msg.what == FAILED) {
                    if (updateTv != null) {

                        updateConfirm.setClickable(true);
                        updateTv.setVisibility(View.VISIBLE);
                        updateProgress.setVisibility(View.GONE);
                    }
                }
            }
        }
    };


    @Override
    public void onStart(int start) {
        progressHandler.sendEmptyMessage(START);

    }

    @Override
    public void onProgress(int progress) {
        progressHandler.sendEmptyMessage(progress);

    }

    @Override
    public void onFinish(String path, byte[] data) {
//        String myMd5 = MD5.getFileMD5(filePath);

//        Logger.i("updateMd5 " + updateBean.getDownlad_md5() + "\n myMd5: " + myMd5);


//        Logger.i("updateMd5 " + updateBean.getDownlad_md5() + "\n myMd5: " + myMd5);
//        if (updateBean?.getDownlad_md5().equals(myMd5)) {
//            Logger.i("DM5解析完毕")
//        } else {
//            Logger.i("DM5不一致")
//        }

        progressHandler.sendEmptyMessage(FINISH);
    }

    @Override
    public void onFail(String errorInfo) {
        Message msg = new Message();
        msg.obj = errorInfo;
        msg.what = FAILED;

        progressHandler.sendMessage(msg);
    }
}
