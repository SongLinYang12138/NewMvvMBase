package com.bondex.photo.main

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import com.bondex.library.app.AppManager
import com.bondex.library.base.BaseActivity
import com.bondex.library.util.CommonUtils
import com.bondex.library.util.NoDoubleClickListener
import com.bondex.library.util.ToastUtils
import com.bondex.photo.BR
import com.bondex.photo.MQAidlCallBack
import com.bondex.photo.MQAidlInterface
import com.bondex.photo.R
import com.bondex.ysl.camera.bean.ImgBean
import com.bondex.photo.databinding.ActivityMainBinding
import com.bondex.photo.log.LogActivity
import com.bondex.photo.mq.MQManager
import com.bondex.photo.service.MqService
import com.bondex.ysl.camera.CameraActivity
import com.bondex.ysl.camera.ISCameraConfig
import com.bondex.ysl.camera.ISNav
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogBean
import com.google.zxing.integration.android.IntentIntegrator
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MenuDialogBuilder
import com.qmuiteam.qmui.widget.dialog.QMUIDialogAction
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val IMG_REQUEST = 1001
    private val REQUEST_CAMERA_PERMISSION = 113
    private val REQUEST_SCAN_PERMISSIOn = 112
    private val REQUEST_CODE_SCAN = 114
    private var btnSize = 0;
    private var mqBind: MQAidlInterface? = null

    private var qrCodeDialog: QMUIDialog? = null
    private val mCurrentDialogStyle = com.qmuiteam.qmui.R.style.QMUI_Dialog

    private val mqServiceListener = object : MQAidlCallBack.Stub() {

        override fun sendMsg(mq: String?) {

        }
    }


    private val qrCodeObserver = Observer<Int> { t ->
        when (t) {

            viewModel.PARSE_FAIL -> {

                ToastUtils.showToast("解码失败，当前二维码格式不对")
            }
            viewModel.PARSE_SUCCESS -> {
                ToastUtils.showToast("解码成功")
            }

            viewModel.TAKE_PHOTO -> {
                toCamera(true)
            }
            MQManager.START_SUCCESS -> {

                content.show("", "MQ启动成功")
            }
            MQManager.START_FAIL -> {
                content.show("mq启动失败", "请重启mq或者检查网络是否在内网中")
            }
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {
            Log.i("aaa", "serviceDisconnected")
        }

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {

            mqBind = service as MQAidlInterface


            Log.i("aaa", "serviceConnected")
        }
    }


    override fun getBindingVariable(): Int {

        return BR.mainModel
    }

    override fun getLayoutId(): Int {

        return R.layout.activity_main
    }

    override fun initView() {

        showLeft(false, 0)
        showTile(true, "拍照凭证工具")

        showRight(true, R.string.camera, object : NoDoubleClickListener() {
            override fun click(v: View?) {

                if (viewModel.haveStart) {
                    toCamera(false)
                } else {
                    ToastUtils.showToast("请先启动mq")
                }
            }
        })

        btnSize = QMUIDisplayHelper.dp2px(this, 56)

        img_btn.borderColor =
            QMUIResHelper.getAttrColor(this, R.attr.qmui_skin_support_color_separator)
        img_btn.setRadiusAndShadow(btnSize / 2, QMUIDisplayHelper.dp2px(this, 16), 0.4f)
        img_btn.setBackgroundColor(
            QMUIResHelper.getAttrColor(
                this,
                R.attr.app_skin_common_background
            )
        )
        version.setText("版本:${CommonUtils.getVersionName(this)}")
        main_out.setOnClickListener { exit() }
        check_update.setOnClickListener {
            viewModel.cheVersion()
        }

    }


    override fun initListener() {
        img_btn.setOnClickListener { _ ->

            MenuDialogBuilder(this)
                .addItems(viewModel.MENU_ITEMS) { dialog, which ->

                    when (which) {

                        0 -> viewModel.restartMq()
                        1 -> showQrCodeDialog()
                        2 -> isAutoTakePhoto(viewModel.autoTakePhoto())
                        3 -> chooseCompressRatio()
                        4 -> chooseTakeDelay()
                        5 -> toLog(true)
                        6 -> toLog(false)
                    }
                    dialog.dismiss()
                }
                .setSkinManager(QMUISkinManager.defaultInstance(this))
                .create()
                .show()
        }
        it_scan.setOnClickListener { _ -> toScan() }
        registerObserver()
    }

    private fun toLog(isMq: Boolean) {

        val intent = Intent(this, LogActivity::class.java)
        intent.putExtra("log", isMq)
        startActivity(intent)

    }

    protected fun exit() {

        AppManager.getInstance().finishAllActivity()
        finish()
    }

    private fun registerObserver() {

        viewModel.qrCodeLiveData.observe(this, qrCodeObserver)
    }


    fun toCamera(isAuto: Boolean): Unit {

        Log.i("aaa", "activity compressRation " + getRealCompressRatio())

        val takeDelay =
            if (viewModel.getTakeDelay() == 0) 0 else viewModel.TAKE_DELAY_ITEMS[viewModel.getTakeDelay()].toInt()
        val config = ISCameraConfig.Builder().setCompressRatio(getRealCompressRatio())
            .setTakeDelay(takeDelay)
            .setAutotake(if (isAuto) viewModel.autoTakePhoto() else false).build()

        ISNav.getInstance().toCamera(this, config, IMG_REQUEST)

    }

    private fun toScan(): Unit {
        //TODO 扫描
        IntentIntegrator(this).setBeepEnabled(true).setRequestCode(REQUEST_CODE_SCAN)
            .initiateScan()

    }

    private fun showQrCodeDialog() {

        if (qrCodeDialog == null) {

            val builder = QMUIDialog.MessageDialogBuilder(this)

            builder?.let { it ->
                it.setTitle("显示二维码")
                    .setMessage(viewModel.getQrCode())
                    .setSkinManager(QMUISkinManager.defaultInstance(this))
                    .addAction("关闭", object : QMUIDialogAction.ActionListener {
                        override fun onClick(dialog: QMUIDialog?, index: Int) {

                            dialog?.dismiss()
                        }
                    })
            }
            qrCodeDialog = builder.create(mCurrentDialogStyle)
            qrCodeDialog?.show()

        } else {

            if (qrCodeDialog?.isShowing!!) {

                qrCodeDialog?.dismiss()
            } else {
                qrCodeDialog?.show()
            }
        }
    }

    private fun isAutoTakePhoto(autoTakePhoto: Boolean) {

        val qmuiDailogBuilder = QMUIDialog.CheckBoxMessageDialogBuilder(this)


        qmuiDailogBuilder.setTitle("当前是否自动拍照?")
            .setMessage("自动拍照")
            .setChecked(autoTakePhoto)
            .addAction(
                "取消"
            ) { dialog, index -> dialog?.dismiss() }
            .addAction("确定") { dialog, index ->

                Log.i("aaa", "auto check " + qmuiDailogBuilder.isChecked)
                viewModel.saveAutoTakePhoto(qmuiDailogBuilder.isChecked)
                dialog.dismiss()
            }
            .create(mCurrentDialogStyle)
            .show()
    }

    private fun showError() {

        val append = StringBuffer()
        val isSuccess: Boolean = viewModel.listUploadFaile.size == 0

        val title = if (isSuccess) {
            "上传成功"
        } else {
            "上传失败"
        }

        for (bean in viewModel.listUploadFaile) {

            val fileTypeIndex: Int = bean.filePath.lastIndexOf("/")
            val fileName = bean.filePath.substring(fileTypeIndex + 1)

            append.append("$fileName - ${bean.content}\n")

        }

        val builder = QMUIDialog.MessageDialogBuilder(this)
            .setTitle(title)
            .setSkinManager(QMUISkinManager.defaultInstance(this))
            .addAction("取消", object : QMUIDialogAction.ActionListener {
                override fun onClick(dialog: QMUIDialog?, index: Int) {
                    dialog?.dismiss()
                }
            })
        if (!isSuccess) {
            builder.setMessage(append.toString())
            builder.addAction("重新上传", object : QMUIDialogAction.ActionListener {
                override fun onClick(dialog: QMUIDialog?, index: Int) {
                    dialog?.dismiss()

                    viewModel.loading.postValue(true)
                    val tmpList = mutableListOf<BusinessLogBean>()
                    tmpList.addAll(viewModel.listUploadFaile)
                    viewModel.clearUploadList()
                    viewModel.uploadIterator(tmpList, false)
                }
            })
        }
        builder.create(mCurrentDialogStyle).show()


    }

    private fun chooseCompressRatio() {


        QMUIDialog.CheckableDialogBuilder(this)
            .setCheckedIndex(chooseCompressRaatioIndex())
            .setSkinManager(QMUISkinManager.defaultInstance(this))
            .addItems(viewModel.COMPRESS_RATIO_ITEMS) { dialog, which ->

                Log.i("aaa", "chooseCompressRatio " + viewModel.COMPRESS_RATIO_ITEMS[which])
                viewModel.saveCompressRatio(which)
                dialog.dismiss()
            }
            .addAction("取消") { dialog, index ->
                dialog.dismiss()
            }
            .create(mCurrentDialogStyle)
            .show()

    }

    private fun chooseTakeDelay() {


        QMUIDialog.CheckableDialogBuilder(this)
            .setCheckedIndex(viewModel.getTakeDelay())
            .setSkinManager(QMUISkinManager.defaultInstance(this))
            .addItems(viewModel.TAKE_DELAY_ITEMS) { dialog, which ->

                Log.i("aaa", "chooseCompressRatio " + viewModel.TAKE_DELAY_ITEMS[which])
                viewModel.saveTakeDelay(which)
                dialog.dismiss()
            }
            .addAction("取消") { dialog, index ->
                dialog.dismiss()
            }
            .create(mCurrentDialogStyle)
            .show()

    }

    private fun chooseCompressRaatioIndex(): Int {

        var index = viewModel.compressRatio()

        if (index == -1) {
            return 0
        }
        return index
    }

    private fun getRealCompressRatio(): Int {

        val which = chooseCompressRaatioIndex()

        var compressRatio = 0
        when (which) {
            0 -> compressRatio = 70
            1 -> compressRatio = 80
            2 -> compressRatio = 95
            3 -> compressRatio = 100
        }
        return compressRatio
    }

    private fun bindService() {

        val intent = Intent(this, MqService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun unbindService() {

        unbindService(serviceConnection)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {

            val result = IntentIntegrator.parseActivityResult(resultCode, data)
            if (result.contents == null) {
            } else {
                viewModel.parseQRCode(result.contents)

            }
        } else if (requestCode == IMG_REQUEST && resultCode == Activity.RESULT_OK) {

            val listImg: ArrayList<ImgBean>? =
                data?.getParcelableArrayListExtra(CameraActivity.FINISH_KEY)



            listImg?.let { it ->

//                val filePath = it.get(0)

                if (listImg.isEmpty()) {
                    ToastUtils.showToast("请先拍照")
                    return
                }
                viewModel.clearUploadList()
                viewModel.uploadImage(listImg)


            }
        }

    }


    override fun onStart() {
        super.onStart()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (viewModel.qrCodeLiveData.hasObservers()) {
            viewModel.qrCodeLiveData.removeObserver(qrCodeObserver)
        }
//        unbindService()

    }

    override fun handleMsg(msg: String?) {

        when (msg) {
            "update" -> viewModel.showUpdateDialog(this)

            "upload" -> showError()


        }

    }

}


