package com.bondex.photo.main

import com.bondex.library.base.BaseBack
import com.bondex.ysl.installlibrary.download.UpdateBean

/**
 * date: 2020/6/2
 * @Author: ysl
 * description:
 */
interface MainCallBack : BaseBack {

        fun checkVersion(updateBean: UpdateBean)

}