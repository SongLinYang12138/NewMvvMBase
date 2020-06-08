package com.bondex.ysl.camera.ui.utils;

import com.bondex.ysl.camera.bean.BitmapBean;
import com.google.zxing.Result;

/**
 * date: 2020/6/5
 *
 * @Author: ysl
 * description:
 */
public interface DecodeBitmapCallback {

    void onImageDecodeSuccess(Result result);

    void onImageDecodeFailed();
}
