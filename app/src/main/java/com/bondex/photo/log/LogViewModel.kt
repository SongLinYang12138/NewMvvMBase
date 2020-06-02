package com.bondex.photo.log

import android.util.Log
import android.view.View
import androidx.databinding.ObservableField
import com.bondex.library.base.BaseViewMode
import com.bondex.library.base.Constant
import com.bondex.library.util.CommonUtils
import com.bondex.library.util.SharedPreferenceUtils
import com.bondex.library.util.ToastUtils
import com.bondex.photo.bean.QRCodeBean
import com.bondex.photo.net.NetWorkUtils
import com.bondex.photo.utils.RecyclerListener
import com.bondex.photo.utils.itf.UploadCallBack
import com.bondex.photo.utils.itf.ViewModelCallBack
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogBean
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogDao
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogDataBase
import com.bondex.ysl.databaselibrary.mqlog.MQLogBean
import com.bondex.ysl.databaselibrary.mqlog.MQLogBeanDatabase
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

/**
 * date: 2020/5/26
 * @Author: ysl
 * description:
 */
class LogViewModel : BaseViewMode<LogModel>(), LogCallBack {
    //    判断当前显示的是MQ日志还是BUSINESS日志
    var isMq = true

    var listBusiness: MutableList<BusinessLogBean> = mutableListOf();
    var listMq: MutableList<MQLogBean> = mutableListOf()

    val businessAdapter = BusinessLogAdapter()
    val mqAdapter = MqLogAdapter()

    var mqSharedPreferences: SharedPreferenceUtils? = null
    var upload_url: String? = null
    var businessLogDao: BusinessLogDao? = null

    val listUploadFaile: MutableList<BusinessLogBean> = mutableListOf()
    val listUploadSuccess: MutableList<BusinessLogBean> = mutableListOf()


    fun readLog(callBack: ViewModelCallBack<String>) {


        val observable = Observable.create<String> {

            if (isMq) {

                val dao = MQLogBeanDatabase.getInstance(context).dao


                listMq = dao.all

                if (listMq == null) listMq = mutableListOf()
                mqAdapter.updateList(listMq)
                it.onNext("Y")
            } else {

                val dao = BusinessLogDataBase.getInstance(context).dao
                listBusiness = dao.all

                if (listBusiness == null) {
                    listBusiness = mutableListOf()
                }
                businessAdapter.updateList(listBusiness)
                it.onNext("Y")
            }


        }
        val consumer = Consumer<String> {

            callBack.logBack("Y")

        }

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)


    }

    fun getQrCode(): String? {
        val qrCode = mqSharedPreferences?.getStr(Constant.SAVE_MQ_KEY)
        return qrCode
    }

    fun setUpload_url(): String {

        val qrStr = getQrCode();
        val gson = Gson()
        val qrCode = gson.fromJson(qrStr, QRCodeBean::class.java)

        if (qrCode == null) return ""

        val clientMqGuid = qrCode.clientMQGuid

        upload_url = "${qrCode.serviceIpAddress}:${qrCode.serviceIpPort}"
        if (!upload_url?.contains("http://")!!) {
            upload_url = "http://${upload_url}"
        }

        return clientMqGuid
    }

    fun reUploadPhoto(callBack: ViewModelCallBack<String>) {

        val observable = Observable.create(object : ObservableOnSubscribe<String> {
            override fun subscribe(emitter: ObservableEmitter<String>) {

                val dao = BusinessLogDataBase.getInstance(context).dao

                val list: MutableList<BusinessLogBean>? = dao.selectNoSuccess()

                if (list == null || list.isEmpty()) {
                    emitter.onNext("null")
                } else {
                    emitter.onNext("start")
                    list?.let { it ->
                        clearUploadList()
                        uploadIterator(it)
                    }
                    Log.i("aaa", " reupload  y")
                    emitter.onNext("y")
                }

            }
        })
        val consumer = Consumer<String> {

            if (it.equals("null")) {
                callBack.logBack("null")
            } else if (it.equals("start")) {
                callBack.logBack("start")
            } else if (it.equals("y")) {
                callBack.logBack("y")
            }

        }

        observable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(consumer)
    }

    fun updateBusinessLog() {

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

    private fun uploadIterator(list: MutableList<BusinessLogBean>) {


        if (list.isEmpty()) {

            updateBusinessLog()
            loading.postValue(false)
            return
        }

        val bean = list.get(0)

        if (upload_url == null) {
            return
        }


        NetWorkUtils.upload(upload_url, bean.filePath, object : UploadCallBack {
            override fun uploadError(error: String, file_path: String) {

                bean.content = error
                bean.status = 1
                listUploadFaile.add(bean)

                list.removeAt(0)
                uploadIterator(list)
            }

            override fun uploadSuccess(msg: String, file_path: String) {
                bean.content = msg
                bean.status = 0
                listUploadSuccess.add(bean)

                list.removeAt(0)
                uploadIterator(list)

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
        businessLogDao = BusinessLogDataBase.getInstance(context).dao

        setUpload_url()
        businessAdapter.listener = object : RecyclerListener<BusinessLogBean> {
            override fun onItem(postion: Int, item: BusinessLogBean?) {

                val list = mutableListOf<BusinessLogBean>()

                if (item != null && item.status != 0) {
                    clearUploadList()
                    list.add(item)
                    loading.postValue(true)
                    uploadIterator(list)
                } else {
                    ToastUtils.showToast("该图片已上传成功")
                }

            }
        }

    }

    override fun attachUi() {
    }

    override fun detachUi() {
    }

    override fun onStart() {
    }

    override fun onDestroy() {
    }

    override fun setMyModel() {

        model = LogModel(this)
    }

}