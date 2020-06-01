// MQAidlInterface.aidl
package com.bondex.photo;
import com.bondex.photo.MQAidlCallBack;
// Declare any non-default types here with import statements

interface MQAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void startMq( );
    void registerMQCallBack(MQAidlCallBack listener);
}
