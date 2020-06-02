package com.bondex.library.base;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * date: 2020/4/23
 *
 * @Author: ysl
 * description:
 */
public abstract class BaseViewMode<T extends BaseModel> extends ViewModel implements LifecycleaWacher {

    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    protected MutableLiveData<String> toastLiveData = new MutableLiveData<>();
    protected MutableLiveData<String> msgLiveData = new MutableLiveData<>();

    protected Context context;
    protected T model;

    protected void setContext(Context context) {
        this.context = context;
        setMyModel();
    }
    protected  abstract void setMyModel();



}
