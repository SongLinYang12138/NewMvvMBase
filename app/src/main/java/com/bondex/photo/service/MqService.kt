package com.bondex.photo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.bondex.library.mq.RabbitMqConfig
import com.bondex.library.mq.RabbitMqUtil
import com.bondex.library.util.MQCallBack
import com.bondex.photo.MQAidlCallBack
import com.bondex.photo.MQAidlInterface
import io.reactivex.Scheduler

/**
 * date: 2020/5/20
 * @Author: ysl
 * description:
 */
class MqService : Service() {

    var listener: MQAidlCallBack? = null

    val mqBinder = object : MQAidlInterface.Stub() {

        override fun registerMQCallBack(listener: MQAidlCallBack?) {

            this@MqService.listener = listener

        }

        override fun startMq() {

            this@MqService.statMq()
        }

    }

    val mqCallBack = object : MQCallBack {
        override fun sendMsg(mq: String?) {
            if (listener != null) {
                listener?.sendMsg(mq)
            }
        }

        override fun startFail(error: String?) {
            Log.i("aaa", " mq startFail")
        }

        override fun startSuccess() {
            Log.i("aaa", "mq startSuccess")
        }
    }

    private fun statMq() {


//        val thread = Thread(Runnable {
//            val builder = RabbitMqConfig.Builder().build()
//            RabbitMqUtil.getInstance(builder).setMqCallBack(mqCallBack).startMQ()
//        })
//        thread.start()

    }

    override fun onBind(intent: Intent?): IBinder? {

        return mqBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }
}