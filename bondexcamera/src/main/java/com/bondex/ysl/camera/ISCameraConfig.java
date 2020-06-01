package com.bondex.ysl.camera;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.ArrayList;

/**
 * date: 2019/8/6
 * Author: ysl
 * description:
 */
public class ISCameraConfig implements Parcelable {

    public int compressRatio;
    public boolean isAutoTak;


    public ISCameraConfig(Builder builder) {
        this.compressRatio = builder.compressRatio;
        this.isAutoTak = builder.isAutoTak;
    }

    protected ISCameraConfig(Parcel in) {
        compressRatio = in.readInt();
        isAutoTak = in.readByte() != 0;
    }

    public static final Creator<ISCameraConfig> CREATOR = new Creator<ISCameraConfig>() {
        @Override
        public ISCameraConfig createFromParcel(Parcel in) {
            return new ISCameraConfig(in);
        }

        @Override
        public ISCameraConfig[] newArray(int size) {
            return new ISCameraConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(compressRatio);
        dest.writeByte((byte) (isAutoTak ? 1 : 0));
    }


    public static class Builder implements Parcelable {
        private int compressRatio;
        public boolean isAutoTak;


        protected Builder(Parcel in) {
            compressRatio = in.readInt();
            isAutoTak = in.readByte() != 0;
        }

        public static final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel in) {
                return new Builder(in);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };

        public Builder setCompressRatio(int compressRatio) {
            this.compressRatio = compressRatio;
            return this;
        }

        public Builder setAutotake(boolean isAutoTak) {
            this.isAutoTak = isAutoTak;
            return this;
        }

        public Builder() {

        }


        public ISCameraConfig build() {

            return new ISCameraConfig(this);
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(compressRatio);
            dest.writeByte((byte) (isAutoTak ? 1 : 0));
        }
    }
}
