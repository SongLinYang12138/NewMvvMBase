package com.bondex.photo.main

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
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
import com.bondex.photo.R
import com.bondex.ysl.camera.bean.ImgBean
import com.bondex.photo.databinding.ActivityMainBinding
import com.bondex.photo.log.LogActivity
import com.bondex.photo.mq.MQManager
import com.bondex.photo.service.MqService
import com.bondex.ysl.camera.CameraActivity
import com.bondex.ysl.camera.ISCameraConfig
import com.bondex.ysl.camera.ISNav
import com.bondex.ysl.camera.decode.DecodeCameraActivity
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogBean
import com.google.zxing.client.android.Intents
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList


class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {

    private val IMG_REQUEST = 1001
    private val REQUEST_CODE_SCAN = 114


    private val qrCodeObserver = Observer<Int> { t ->
        when (t) {

            MQManager.START_FAIL -> {
            }
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

            }
        })

        viewModel.createManager(supportFragmentManager,R.id.content)

    }


    override fun initListener() {

        bt_fisrt.setOnClickListener(clickListener)
        bt_second.setOnClickListener(clickListener)

    }


    override fun onDestroy() {
        super.onDestroy()

    }

    override fun handleMsg(msg: String?) {


    }

    override fun myClick(v: View?) {

        when (v?.id) {

            R.id.bt_fisrt -> {

                viewModel.switchFragment(0)
            }
            R.id.bt_second -> {

                viewModel.switchFragment(1)
            }

        }

    }

}


