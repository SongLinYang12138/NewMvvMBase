package com.bondex.library.base;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * date: 2020/4/23
 *
 * @Author: ysl
 * description:
 */
public abstract class BaseViewMode extends ViewModel implements LifecycleaWacher {

    protected MutableLiveData<Boolean> loading = new MutableLiveData<>();


}
