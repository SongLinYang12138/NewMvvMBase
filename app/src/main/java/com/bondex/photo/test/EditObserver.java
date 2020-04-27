package com.bondex.photo.test;

import android.util.Log;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.bondex.photo.BR;

/**
 * date: 2020/4/26
 *
 * @Author: ysl
 * description:
 */
public class EditObserver extends BaseObservable {
    @Bindable
    public String et = "123455";
    @Bindable
    public boolean check = true;

    public String getEt() {

        return et;
    }

    public void setEt(String et) {

        this.et = et;
        Log.i("aaa", "et  " + et);
        notifyPropertyChanged(BR.et);
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
        Log.i("aaa", "check"+check);
        notifyPropertyChanged(BR.check);
    }
}
