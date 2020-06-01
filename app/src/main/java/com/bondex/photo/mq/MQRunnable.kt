package com.bondex.photo.mq

import com.bondex.library.mq.RabbitMqConfig
import com.bondex.library.mq.RabbitMqUtil
import com.bondex.library.util.MQCallBack
import com.bondex.library.util.MQShutodwnListener
import java.lang.NullPointerException

/**
 * date: 2020/5/20
 * @Author: ysl
 * description:
 */
class MQRunnable(clientMqGuid:String) : Runnable {


    var listener: MQCallBack? = null
    var rabbitMqUtil: RabbitMqUtil? = null
    var mqShutodwn:MQShutodwnListener? = null

    init {

        val config = RabbitMqConfig.Builder(clientMqGuid).build()
        rabbitMqUtil = RabbitMqUtil(config)
    }


    fun updateClientMqGuid(clientMqGuid: String){

        val config = RabbitMqConfig.Builder(clientMqGuid).build()
        rabbitMqUtil = RabbitMqUtil(config)

    }

    override fun run() {

        if (listener == null) {
            throw NullPointerException("MQCallBack  is null")
        }

        if(mqShutodwn == null){
            throw NullPointerException("MQShutDownListener is null")
        }


        rabbitMqUtil?.setMqCallBack(listener)
        rabbitMqUtil?.startMQ()
    }

    fun mqConnected(): Boolean {

        return rabbitMqUtil!!.isConnected
    }


}