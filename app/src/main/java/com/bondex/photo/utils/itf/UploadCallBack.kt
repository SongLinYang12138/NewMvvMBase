package com.bondex.photo.utils.itf

/**
 * date: 2020/5/25
 * @Author: ysl
 * description:
 */
interface UploadCallBack {

    fun uploadError(error: String, file_path: String)

    fun uploadSuccess(msg: String, file_path: String)
}