package com.bondex.library.util;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.res.Configuration;
import android.util.DisplayMetrics;

import androidx.annotation.NonNull;

/**
 * date: 2020/12/2
 *
 * @author: ysl
 * description:
 */
public class ScreenUtil {

    private static float sNoncompatDensity;
    private static float sNoncompatScalDensity;

    public static void adapterScreen(@NonNull Activity activity, @NonNull final Application application) {

        final DisplayMetrics appDisplayMetrics = application.getResources().getDisplayMetrics();
        if (sNoncompatDensity == 0) {
            sNoncompatDensity = appDisplayMetrics.density;
            sNoncompatScalDensity = appDisplayMetrics.scaledDensity;
            application.registerComponentCallbacks(new ComponentCallbacks() {
                @Override
                public void onConfigurationChanged(@NonNull Configuration newConfig) {

                    if (newConfig != null && newConfig.fontScale > 0) {
                        sNoncompatScalDensity = application.getResources().getDisplayMetrics().scaledDensity;
                    }

                }

                @Override
                public void onLowMemory() {

                }
            });
        }

        final float targetDensity = appDisplayMetrics.widthPixels / 360;
        final float targetScaledDensity = targetDensity * (sNoncompatScalDensity / sNoncompatDensity);
        final int targetDensityDpi = (int) (160 * targetDensity);

        appDisplayMetrics.density = targetDensity;
        appDisplayMetrics.scaledDensity = targetScaledDensity;
        appDisplayMetrics.densityDpi = targetDensityDpi;

        final DisplayMetrics activityDispalyMertrics = activity.getResources().getDisplayMetrics();
        activityDispalyMertrics.density = targetDensity;
        activityDispalyMertrics.scaledDensity = targetScaledDensity;
        activityDispalyMertrics.densityDpi = targetDensityDpi;

    }

}
