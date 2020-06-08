package com.bondex.ysl.camera.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * date: 2020/6/5
 *
 * @Author: ysl
 * description:
 */
public class BitmapBean implements Parcelable{


    private String path;
    private int id;
    private String fileName;
    private String qrCode;

    public BitmapBean() {
    }

    public BitmapBean(String path, int id, String fileName, String qrCode) {
        this.path = path;
        this.id = id;
        this.fileName = fileName;
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode == null ? "" : qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode == null ? "" : qrCode;
    }

    protected BitmapBean(Parcel in) {
        path = in.readString();
        id = in.readInt();
        fileName = in.readString();
        qrCode = in.readString();
    }

    public static final Creator<BitmapBean> CREATOR = new Creator<BitmapBean>() {
        @Override
        public BitmapBean createFromParcel(Parcel in) {
            return new BitmapBean(in);
        }

        @Override
        public BitmapBean[] newArray(int size) {
            return new BitmapBean[size];
        }
    };


    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path == null ? "" : path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName == null ? "" : fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? "" : fileName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeInt(id);
        dest.writeString(fileName);
        dest.writeString(qrCode);
    }
}
