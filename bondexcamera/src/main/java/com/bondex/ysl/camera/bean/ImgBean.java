package com.bondex.ysl.camera.bean;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

/**
 * date: 2020/6/3
 *
 * @Author: ysl
 * description:
 */
public class ImgBean implements Parcelable {

    private String path;
    private String qrCode;
    private String fileName;
    private int id;


    public ImgBean() {

    }


    public ImgBean(String path, String qrCode, String fileName, int id) {
        this.path = path;
        this.qrCode = qrCode;
        this.fileName = fileName;
        this.id = id;
    }

    protected ImgBean(Parcel in) {
        path = in.readString();
        qrCode = in.readString();
        fileName = in.readString();
        id = in.readInt();
    }

    public static final Creator<ImgBean> CREATOR = new Creator<ImgBean>() {
        @Override
        public ImgBean createFromParcel(Parcel in) {
            return new ImgBean(in);
        }

        @Override
        public ImgBean[] newArray(int size) {
            return new ImgBean[size];
        }
    };

    public String getPath() {
        return path == null ? "" : path;
    }

    public void setPath(String path) {
        this.path = path == null ? "" : path;
    }

    public String getQrCode() {
        return qrCode == null ? "" : qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode == null ? "" : qrCode;
    }

    public String getFileName() {
        return fileName == null ? "" : fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName == null ? "" : fileName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(@Nullable Object obj) {

        if (obj == null) {
            return false;
        }

        ImgBean bean = (ImgBean) obj;
        if (this == null || this.fileName == null) {
            return false;
        }


        return this.fileName.equals(bean.getFileName());
    }

    @Override
    public int hashCode() {
        return this.fileName.hashCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(qrCode);
        dest.writeString(fileName);
        dest.writeInt(id);
    }
}
