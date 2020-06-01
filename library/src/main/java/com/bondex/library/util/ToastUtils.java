package com.bondex.library.util;

import android.widget.Toast;

import com.bondex.library.app.PhotoApplication;

/**
 * date: 2020/4/28
 *
 * @Author: ysl
 * description:
 */
public class ToastUtils {

    public static void showToast(String msg) {
        if (CommonUtils.isEmpty(msg)) {
            return;
        }


        Toast.makeText(PhotoApplication.getContext(), msg, Toast.LENGTH_SHORT).show();


    }
}
