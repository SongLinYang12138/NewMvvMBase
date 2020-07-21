package com.bondex.photo.main

import android.app.Dialog
import android.content.Context
import android.os.Environment
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.bondex.library.app.PhotoApplication
import com.bondex.library.base.BaseViewMode
import com.bondex.library.base.Constant
import com.bondex.library.mq.MQBean
import com.bondex.library.ui.ProgressView
import com.bondex.library.util.CommonUtils
import com.bondex.library.util.SharedPreferenceUtils
import com.bondex.library.util.ToastUtils
import com.bondex.photo.R
import com.bondex.ysl.camera.bean.ImgBean
import com.bondex.photo.bean.QRCodeBean
import com.bondex.photo.mq.MQCheckWork
import com.bondex.photo.mq.MQManager
import com.bondex.photo.net.NetWorkUtils
import com.bondex.photo.utils.MD5
import com.bondex.photo.utils.itf.UploadCallBack
import com.bondex.photo.utils.runnable.BusinessLogRunnable
import com.bondex.photo.utils.runnable.MQLoginBeanRunnable
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogBean
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogDao
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogDataBase
import com.bondex.ysl.databaselibrary.mqlog.MQLogBean
import com.bondex.ysl.databaselibrary.mqlog.MQLogBeanDao
import com.bondex.ysl.databaselibrary.mqlog.MQLogBeanDatabase
import com.bondex.ysl.installlibrary.InstallApk
import com.bondex.ysl.installlibrary.download.DownloadListener
import com.bondex.ysl.installlibrary.download.UpdateBean
import com.google.gson.Gson
import com.google.zxing.Result
import com.journeyapps.barcodescanner.utils.DecodeImgThread
import com.journeyapps.barcodescanner.inter.DecodeImgCallback
import com.orhanobut.logger.Logger
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * date: 2020/5/20
 * @Author: ysl
 * description:
 */
class MainViewModel : BaseViewMode<MainModel>(), MainCallBack, DownloadListener {



    var upload_url: String? = null

    private var updateTv: TextView? = null
    private var updateProgress: ProgressView? = null
    private var updateConfirm: Button? = null
    private var updateDialog: Dialog? = null
    protected var filePath: String? = null
    protected var updateBean: UpdateBean? = null




    override fun onResume() {


    }

    override fun onStop() {
    }

    override fun onCreate() {



        filePath =
            context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath + File.separator + "photo.apk"
    }

    fun cheVersion() {

        model.checkVersion()
    }


    override fun attachUi() {
    }

    override fun detachUi() {
    }


    override fun onStart() {
        cheVersion()
    }

    override fun onDestroy() {
    }

    override fun checkVersion(bean: UpdateBean) {

        updateBean = bean;
//        对比version code 检查是否需要更新
        if (bean.getVersion_remark().contains("type")) {

            try {
                val ob = JSONObject(bean.getVersion_remark())
                val remark: String = ob.getString("remark")
                val type: String = ob.getString("type")
                if (type == "photo") {
                    bean.setDescription(remark)
                    msgLiveData.postValue("update")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }


    }


    fun showUpdateDialog(context: Context) {

        if (updateDialog == null) {
            updateDialog = Dialog(context, R.style.dialog)
            val view: View = LayoutInflater.from(context)
                .inflate(R.layout.dialog_update_layout, null)
            updateDialog!!.setContentView(view)
            val baseBack =
                view.findViewById<ImageView>(R.id.base_back)
            updateConfirm = view.findViewById<Button>(R.id.update_confirm)
            val updateCancel =
                view.findViewById<Button>(R.id.update_cancel)
            updateTv = view.findViewById<TextView>(R.id.update_tv)
            updateProgress = view.findViewById(R.id.update_progress)
            baseBack.setOnClickListener { updateDialog!!.dismiss() }
            updateConfirm?.setOnClickListener(View.OnClickListener {
                updateConfirm?.setClickable(false)
                model.download(updateBean?.getDownload_url(), filePath, this)
            })
            updateCancel.setOnClickListener { updateDialog!!.dismiss() }
            updateTv?.setText(updateBean?.description)
            val lp =
                updateDialog!!.window!!.attributes
            lp.width = CommonUtils.getScreenW(context) - 20
            val height: Int = CommonUtils.getScreenH(context)
            lp.height = height - height / 4
            lp.gravity = Gravity.CENTER
            updateDialog!!.window!!.attributes = lp
            updateDialog!!.setCanceledOnTouchOutside(true)
        }
        updateDialog!!.show()
    }

    override fun setMyModel() {

        this.model = MainModel(this)

    }

    override fun onStart(start: Int) {
        progressHandler.sendEmptyMessage(START)


    }

    override fun onFinish(path: String?, data: ByteArray?) {

        val myMd5: String = MD5.getFileMD5(filePath)

//        Logger.i("updateMd5 " + updateBean.getDownlad_md5() + "\n myMd5: " + myMd5);


//        Logger.i("updateMd5 " + updateBean.getDownlad_md5() + "\n myMd5: " + myMd5);
        if (updateBean?.getDownlad_md5().equals(myMd5)) {
            Logger.i("DM5解析完毕")
        } else {
            Logger.i("DM5不一致")
        }

        progressHandler.sendEmptyMessage(FINISH)

    }

    override fun onFail(errorInfo: String?) {


        val msg = Message()
        msg.obj = errorInfo
        msg.what = FAILED

        progressHandler.sendMessage(msg)
    }

    override fun onProgress(progress: Int) {
        progressHandler.sendEmptyMessage(progress)
    }

    private val START = 1001
    private val FINISH = 1100
    private val FAILED = 1110
    private val progressHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            if (msg.what != START && msg.what != FINISH && msg.what != FAILED) {
                updateProgress!!.setCurrentValue(msg.what.toFloat())
            } else {
                when (msg.what) {
                    START -> if (updateTv != null) {
                        updateTv!!.visibility = View.GONE
                        updateProgress!!.visibility = View.VISIBLE
                    }
                    FINISH -> if (updateTv != null) {
                        updateDialog!!.dismiss()
                        ToastUtils.showToast("下载成功")
                        updateTv!!.visibility = View.VISIBLE
                        updateProgress!!.visibility = View.GONE
                        updateConfirm!!.isClickable = true
                        InstallApk.install(filePath, context)
                    }
                    FAILED -> if (updateTv != null) {
                        if (CommonUtils.isNotEmpty(msg.obj.toString())) ToastUtils.showToast(
                            msg.obj.toString()
                        )
                        updateConfirm!!.isClickable = true
                        updateTv!!.visibility = View.VISIBLE
                        updateProgress!!.visibility = View.GONE
                    }
                }
            }
        }
    }

}