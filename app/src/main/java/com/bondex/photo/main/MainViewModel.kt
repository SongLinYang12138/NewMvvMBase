package com.bondex.photo.main

import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bondex.library.app.PhotoApplication
import com.bondex.library.base.BaseViewMode
import com.bondex.library.base.Constant
import com.bondex.library.mq.MQBean
import com.bondex.library.util.CommonUtils
import com.bondex.library.util.SharedPreferenceUtils
import com.bondex.library.util.ToastUtils
import com.bondex.library.util.Tools
import com.bondex.photo.bean.QRCodeBean
import com.bondex.photo.mq.MQCheckWork
import com.bondex.photo.mq.MQManager
import com.bondex.photo.net.NetWorkUtils
import com.bondex.photo.utils.itf.ViewModelCallBack
import com.bondex.photo.utils.itf.UploadCallBack
import com.bondex.photo.utils.runnable.BusinessLogRunnable
import com.bondex.photo.utils.runnable.MQLoginBeanRunnable
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogBean
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogDao
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogDataBase
import com.bondex.ysl.databaselibrary.mqlog.MQLogBean
import com.bondex.ysl.databaselibrary.mqlog.MQLogBeanDao
import com.bondex.ysl.databaselibrary.mqlog.MQLogBeanDatabase
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * date: 2020/5/20
 * @Author: ysl
 * description:
 */
class MainViewModel : BaseViewMode() {

    val MENU_ITEMS =
        arrayOf("重启mq", "显示二维码", "是否自动拍照", "压缩比率", "查看MQ日志", "查看上传日志")

    val COMPRESS_RATIO_ITEMS = arrayOf("60%", "75%", "85%", "90%")
    val TAKE_DELAY_ITEMS = arrayOf(3,5,10)

    val PARSE_SUCCESS: Int = 200
    val PARSE_FAIL: Int = 404
    val TAKE_PHOTO: Int = -1;
    val AUTO_TAKE_PHOTO_KEY = "auto_key";
    val COMPRESS_RATIO_KEY = "compress_ratio"


    var upload_url: String? = null
    val qrCodeLiveData = MutableLiveData<Int>()
    var mqSharedPreferences: SharedPreferenceUtils? = null
    var mqLogDao: MQLogBeanDao? = null
    var businessLogDao: BusinessLogDao? = null

    val mqLogRunnable = MQLoginBeanRunnable()
    val businessRunnable = BusinessLogRunnable()

    val listUploadFaile: MutableList<BusinessLogBean> = mutableListOf()
    val listUploadSuccess: MutableList<BusinessLogBean> = mutableListOf()


    var mqLogBean: MQLogBean? = null

    var haveStart: Boolean = false;


    val resultHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)

            when (msg.what) {
                MQManager.START_SUCCESS -> {

                    haveStart = true
                    toastLiveData.postValue("MQ启动成功")
                    qrCodeLiveData.postValue(MQManager.START_SUCCESS)
                    startCheck()
                }
                MQManager.START_FAIL -> {

                    ToastUtils.showToast("MQ启动失败")
                    qrCodeLiveData.postValue(MQManager.START_FAIL)
                }

                MQManager.SHUTDOWN -> {
                    ToastUtils.showToast("MQ异常关闭")
                }

                MQManager.TAKE_PHOTO -> {
                    ToastUtils.showToast("正在准备拍照")
                    val mqMsg = msg.obj.toString();
                    parseMqMsg(mqMsg)
                    Log.i("aaa", "resultHandler $mqMsg")
                }
            }

        }

    }


    private fun startMq(clientMqGuid: String) {

        MQManager.getInstance(clientMqGuid).setResultHandler(resultHandler).doWork();

    }


    fun parseQRCode(qrStr: String) {

        val gson = Gson()

        val qrCode = gson.fromJson(qrStr, QRCodeBean::class.java)

        if (qrCode == null) {
            qrCodeLiveData.postValue(PARSE_FAIL)
        } else {
            upload_url = "${qrCode.serviceIpAddress}:${qrCode.serviceIpPort}"

            if (!upload_url?.contains("http://")!!) {
                upload_url = "http://${upload_url}"
            }

            startMq(qrCode.clientMQGuid)
            Log.i("aaa", "qrcode url $upload_url")
            qrCodeLiveData.postValue(PARSE_SUCCESS)
            mqSharedPreferences?.saveStr(Constant.SAVE_MQ_KEY, qrStr)

        }
    }

    private fun startCheck() {

        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val mqWorkRequest =
            PeriodicWorkRequest.Builder(MQCheckWork::class.java, 15, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .build()
        WorkManager.getInstance(PhotoApplication.getContext()).enqueue(mqWorkRequest)
    }

    fun setUpload_url(): String {

        val qrStr = getQrCode();
        val gson = Gson()
        val qrCode = gson.fromJson(qrStr, QRCodeBean::class.java) ?: return ""
        val clientMqGuid = qrCode.clientMQGuid

        upload_url = "${qrCode.serviceIpAddress}:${qrCode.serviceIpPort}"
        if (!upload_url?.contains("http://")!!) {
            upload_url = "http://${upload_url}"
        }

        return clientMqGuid
    }

    fun restartMq() {

        val clientMqGuid = setUpload_url()

        if (CommonUtils.isNotEmpty(clientMqGuid)) {

            clientMqGuid?.let { it -> startMq(it) }
        } else {
            ToastUtils.showToast("未获取到clientMqGuid重启失败")
        }

    }

    fun getQrCode(): String? {
        val qrCode = mqSharedPreferences?.getStr(Constant.SAVE_MQ_KEY)
        return qrCode
    }

    fun saveCompressRatio(compressRatio: Int) {

        mqSharedPreferences?.saveInt(COMPRESS_RATIO_KEY, compressRatio)
    }

    fun saveAutoTakePhoto(autoTakePhoto: Boolean) {
        mqSharedPreferences?.saveBoolean(AUTO_TAKE_PHOTO_KEY, autoTakePhoto)
    }

    fun compressRatio(): Int {

        val compressRatio: Int = mqSharedPreferences?.getInt(COMPRESS_RATIO_KEY) as Int
        if (compressRatio!! < 0) {
            return 60
        }

        return compressRatio
    }

    fun uploadImage(list: MutableList<String>) {


        if (list.isEmpty()) {

            upadeBusinessLog()
            loading.postValue(false)
            msgLiveData.postValue("upload")
            return
        }

        var seqNo = mqLogBean?.seqNo

        if (seqNo == null) seqNo = System.currentTimeMillis().toString()
        val filePath = list.get(0)

        NetWorkUtils.upload(upload_url, filePath, object : UploadCallBack {
            override fun uploadError(error: String, file_path: String) {


                val bean = BusinessLogBean()

                bean.seqno = seqNo
                bean.filePath = filePath
                bean.status = 1
                bean.content = error

                listUploadFaile.add(bean)

                list.removeAt(0)
                uploadImage(list)
            }

            override fun uploadSuccess(msg: String, file_path: String) {


                val bean = BusinessLogBean()

                bean.seqno = seqNo
                bean.filePath = filePath
                bean.status = 0
                bean.content = "提交成功"

                listUploadSuccess.add(bean)

                list.removeAt(0)
                uploadImage(list)
            }
        })

    }

    fun autoTakePhoto(): Boolean {

        val autoTakePhot: Boolean = mqSharedPreferences?.getBoolean(AUTO_TAKE_PHOTO_KEY)!!
        return autoTakePhot
    }

    fun parseMqMsg(str: String) {
        val gson = Gson()
        val mqBean: MQBean? = gson.fromJson(str, MQBean::class.java)

        mqBean?.let { it ->

            qrCodeLiveData.postValue(TAKE_PHOTO)
            mqLogBean = it.toMqLogBean()
            saveMQMsg()
        }

    }

    fun saveMQMsg() {

        mqLogRunnable.mqLogBean = mqLogBean
        val thread = Thread(mqLogRunnable)
        thread.start()

    }


    fun upadeBusinessLog() {

        val thread = Thread() {

            val tmpList: MutableList<BusinessLogBean> = mutableListOf()
            tmpList.addAll(listUploadFaile)
            tmpList.addAll(listUploadSuccess)

            for (bean in tmpList) {

                var businessLogBean = businessLogDao?.selectBySeqNo(bean.seqno)

                if (businessLogBean == null) {


                    businessLogDao?.insert(bean)
                } else {

                    businessLogBean.content = bean.content
                    businessLogBean.status = bean.status
                    businessLogDao?.update(businessLogBean)
                }
            }

        }
        thread.start()


    }

    private fun deleteLastWeek() {

        val c = Calendar.getInstance()
        c.time = Date()
        c.add(Calendar.DATE, -7)

        val lastWeek = c.time.time

        val thread = Thread() {

            businessLogDao?.deletSuccessBeforTime(lastWeek)
            mqLogDao?.deleteBefor(lastWeek)


        }
        thread.start()

    }

    fun reUploadPhoto() {

        clearUploadList()

        if (CommonUtils.isEmpty(upload_url) && CommonUtils.isNotEmpty(getQrCode())) {
            setUpload_url()
        } else {
            return
        }

        val observable = Observable.create(object : ObservableOnSubscribe<StringBuffer> {
            override fun subscribe(emitter: ObservableEmitter<StringBuffer>) {

                val dao = BusinessLogDataBase.getInstance(context).dao

                val list: MutableList<BusinessLogBean>? = dao.selectNoSuccess()
                Log.i("aaa", " 自动上传 " + list?.size)
                list?.let { it -> uploadIterator(it,true) }
            }
        })
        val consumer = Consumer<StringBuffer> {

        }

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
    }

     fun uploadIterator(list: MutableList<BusinessLogBean>,isAuto:Boolean) {

        if (list.isEmpty()) {
            upadeBusinessLog()
            if(!isAuto){
                loading.postValue(false)
                msgLiveData.postValue("upload")
            }
            return
        }

        val bean = list.get(0)

        if (upload_url == null) {
        }


        NetWorkUtils.upload(upload_url, bean.filePath, object : UploadCallBack {
            override fun uploadError(error: String, file_path: String) {


                bean.content = error
                bean.status = 1
                listUploadFaile.add(bean)
                list.removeAt(0)
                uploadIterator(list,isAuto)
            }

            override fun uploadSuccess(msg: String, file_path: String) {
                bean.content = msg
                bean.status = 0
                listUploadSuccess.add(bean)
                list.removeAt(0)
                uploadIterator(list,isAuto)

            }
        })


    }

    fun clearUploadList() {

        listUploadSuccess.clear()
        listUploadFaile.clear()

    }

    override fun onResume() {
    }

    override fun onStop() {
    }

    override fun onCreate() {

        mqSharedPreferences =
            SharedPreferenceUtils(context, Constant.SAVE_MQ_PAGE)

        mqLogDao = MQLogBeanDatabase.getInstance(context).dao
        businessLogDao = BusinessLogDataBase.getInstance(context).dao

        mqLogRunnable.mqLogDao = mqLogDao
        businessRunnable.dao = businessLogDao

        reUploadPhoto()


        deleteLastWeek()
    }


    override fun attachUi() {
    }

    override fun detachUi() {
    }

    override fun onStart() {

    }

    override fun onDestroy() {
    }

}