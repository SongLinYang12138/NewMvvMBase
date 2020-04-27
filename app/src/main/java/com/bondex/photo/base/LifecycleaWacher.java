package com.bondex.photo.base;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

/**
 * date: 2019/5/28
 * Author: ysl
 * description:
 */
public interface LifecycleaWacher extends LifecycleObserver {


    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate();

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume();

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart();

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop();

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy();

    void attachUi();

    void detachUi();

}
