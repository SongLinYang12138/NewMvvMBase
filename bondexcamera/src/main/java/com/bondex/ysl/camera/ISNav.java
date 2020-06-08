package com.bondex.ysl.camera;

import android.app.Activity;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


/**
 * date: 2019/8/6
 * Author: ysl
 * description:
 */
public class ISNav {

    private static ISNav instance;

    public static ISNav getInstance() {

        if (instance == null) {

            synchronized (ISNav.class) {

                if (instance == null) {
                    instance = new ISNav();
                }

            }


        }

        return instance;

    }

    public void toCamera(Object source, ISCameraConfig config, int requestCode) {


        if (source instanceof Activity) {

            CameraActivity.startActivityForResult((Activity) source, config, requestCode);
        } else if (source instanceof Fragment) {

            CameraActivity.startActivityFroResult((Fragment) source, config, requestCode);
        }


    }


}
