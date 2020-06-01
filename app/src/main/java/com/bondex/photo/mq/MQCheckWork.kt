package com.bondex.photo.mq

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

/**
 * date: 2020/5/20
 * @Author: ysl
 * description:
 */
class MQCheckWork(context: Context, workerParams: WorkerParameters) :
    Worker(context, workerParams) {

    override fun doWork(): Result {

        MQManager.getInstance(null).checkMQ()

        Log.i("aaa", "MQCheckWork")

        return Result.success()
    }

}