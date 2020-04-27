package com.bondex.ysl.camera;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;

/**
 * date: 2019/8/6
 * Author: ysl
 * description:
 */
public class ISCameraConfig{

    private ArrayList<String> hawbs;



    public ISCameraConfig(Builder builder) {

        this.hawbs = builder.hawbBeans;
    }



    public ArrayList<String> getHawbs() {
        if (hawbs == null) {
            return new ArrayList<>();
        }
        return hawbs;
    }



    public static class Builder  {

        private ArrayList<String> hawbBeans;



        public Builder setHawbBeans(ArrayList<String> hawbBeans) {
            this.hawbBeans = hawbBeans;

            return this;
        }




        public ISCameraConfig build() {

            return new ISCameraConfig(this);
        }

    }
}
