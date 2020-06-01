package com.bondex.photo.utils.runnable

import android.util.Log
import com.bondex.ysl.databaselibrary.mqlog.MQLogBean
import com.bondex.ysl.databaselibrary.mqlog.MQLogBeanDao

/**
 * date: 2020/5/22
 * @Author: ysl
 * description:
 */
class MQLoginBeanRunnable : Runnable {

    var mqLogBean: MQLogBean? = null
    var mqLogDao: MQLogBeanDao? = null

    override fun run() {

        if (mqLogBean == null || mqLogDao == null) return


        mqLogBean?.let { it -> mqLogDao?.insert(it) }
        Log.i("aaa", "保存成功")
    }
}