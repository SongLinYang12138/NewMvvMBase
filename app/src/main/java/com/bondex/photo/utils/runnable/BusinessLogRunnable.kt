package com.bondex.photo.utils.runnable

import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogBean
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogDao

/**
 * date: 2020/5/22
 * @Author: ysl
 * description:
 */
class BusinessLogRunnable : Runnable {

    var dao: BusinessLogDao? = null
    var bean: BusinessLogBean? = null
    /**
     * 0 保存
     * */

    override fun run() {

        if(dao == null || bean == null){
            return
        }

        dao?.insert(bean)


    }
}