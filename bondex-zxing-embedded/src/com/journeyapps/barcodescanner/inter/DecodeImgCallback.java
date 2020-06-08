package com.journeyapps.barcodescanner.inter;

import com.google.zxing.Result;

/**
 * date: 2020/6/3
 *
 * @Author: ysl
 * description:
 */
public interface DecodeImgCallback {

    void onImageDecodeSuccess(Result result);

    void onImageDecodeFailed();
}
