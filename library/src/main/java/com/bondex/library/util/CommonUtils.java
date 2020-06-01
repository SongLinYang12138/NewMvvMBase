package com.bondex.library.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.EditText;

import java.lang.reflect.Method;

/**
 * date: 2019/6/19
 * Author: ysl
 * description:
 */
public class CommonUtils {


    public static int getScreenW(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
//        float denisty = dm.density;

        return width;


    }

    public static int getScreenH(Context context) {


        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);

        int height = dm.heightPixels;

        return height;
    }

    public static boolean isEmpty(String str) {

        if (str == null || str.isEmpty()) return true;
        return false;
    }

    public static boolean isNotEmpty(String str) {

        if (str == null || str.isEmpty()) return false;
        return true;
    }



    public static String getVersionName(Context context) {

        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo info = packageManager.getPackageInfo(context.getPackageName(), 0);

            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static int getVersionCode(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public static boolean isNumber(String str) {

        try {

            Double.valueOf(str);
            return true;
        } catch (Exception e) {

            return false;
        }
    }


    /**
     * ==== 隐藏系统键盘 ======
     */
    //用这个方法关闭系统键盘就不会出现光标消失的问题了
    public static void hideSoftInputMethod(EditText ed) {


        String methodName = null;
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        if (currentVersion >= 16) {
            // 4.2
            methodName = "setShowSoftInputOnFocus";  //
        } else if (currentVersion >= 14) {
            // 4.0
            methodName = "setSoftInputShownOnFocus";
        }

        if (methodName == null) {
            //最低级最不济的方式，这个方式会把光标给屏蔽
            ed.setInputType(InputType.TYPE_NULL);
        } else {
            Class<EditText> cls = EditText.class;
            Method setShowSoftInputOnFocus;
            try {
                setShowSoftInputOnFocus = cls.getMethod(methodName, boolean.class);
                setShowSoftInputOnFocus.setAccessible(true);
                setShowSoftInputOnFocus.invoke(ed, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
