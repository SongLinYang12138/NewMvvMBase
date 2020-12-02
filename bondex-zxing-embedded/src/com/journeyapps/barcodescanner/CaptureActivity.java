package com.journeyapps.barcodescanner;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bondex.library.base.Constant;
import com.bondex.library.util.StatusBarUtil;
import com.bondex.library.util.ToastUtils;
import com.google.zxing.Result;
import com.google.zxing.client.android.R;
import com.journeyapps.barcodescanner.inter.DecodeImgCallback;
import com.journeyapps.barcodescanner.utils.DecodeImgThread;
import com.journeyapps.barcodescanner.utils.ImageUtil;

/**
 *
 */
public class CaptureActivity extends AppCompatActivity {
    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ImageButton ivPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        barcodeScannerView = initializeContent();

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        ivPicture = findViewById(R.id.iv_photo);

        ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                toCamera();
            }
        });

        initStatusBar();
    }

    private void toCamera() {

        try {
            /*打开相册*/
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, Constant.REQUEST_IMAGE);
        }catch (ActivityNotFoundException e){
            ToastUtils.showToast("未找到相应的activity");
        }
    }

    private void initStatusBar() {

        StatusBarUtil.setColor(this, getResources().getColor(R.color.black_panit));
    }


    /**
     * Override to use a different layout.
     *
     * @return the DecoratedBarcodeView
     */
    protected DecoratedBarcodeView initializeContent() {
        setContentView(R.layout.zxing_capture);
        return (DecoratedBarcodeView) findViewById(R.id.zxing_barcode_scanner);
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        capture.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * @param rawResult 返回的扫描结果
     */
    public void handleDecode(Result rawResult) {

        BarcodeResult result = new BarcodeResult(rawResult, null);

        capture.returnResult(result);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constant.REQUEST_IMAGE && resultCode == RESULT_OK) {
            String path = ImageUtil.getImageAbsolutePath(this, data.getData());


            new DecodeImgThread(path, new DecodeImgCallback() {
                @Override
                public void onImageDecodeSuccess(Result result) {
                    handleDecode(result);
                    Log.i("aaa", " camera jiexi " + result.getText());
                }

                @Override
                public void onImageDecodeFailed() {
                    capture.returnResultTimeout();
                    Log.i("aaa", " camera jiexi faile");

                }
            }).run();


        }
    }
}
