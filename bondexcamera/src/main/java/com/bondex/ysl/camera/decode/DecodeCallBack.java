package com.bondex.ysl.camera.decode;

import android.graphics.Bitmap;

/**
 * date: 2020/6/10
 *
 * @Author: ysl
 * description:
 */
public interface DecodeCallBack {

    void onPreview(Bitmap bitmap);

    void onPreviewError(Exception e);
}
