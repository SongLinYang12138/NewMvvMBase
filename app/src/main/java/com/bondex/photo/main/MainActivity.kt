package com.bondex.photo.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import com.bondex.library.base.BaseActivity
import com.bondex.library.util.StatusBarUtil
import com.bondex.library.util.ToastUtils
import com.bondex.photo.BR
import com.bondex.photo.R
import com.bondex.photo.databinding.ActivityMainBinding
import com.bondex.ysl.camera.CameraActivity
import com.bondex.ysl.camera.ISNav
import com.google.zxing.integration.android.IntentIntegrator
import com.qmuiteam.qmui.skin.QMUISkinManager
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.qmuiteam.qmui.util.QMUIResHelper
import com.qmuiteam.qmui.widget.dialog.QMUIDialog.MenuDialogBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding, MainViewModel>() {
    private final val IMG_REQUEST = 1001
    private val REQUEST_CAMERA_PERMISSION = 113
    private val REQUEST_SCAN_PERMISSIOn = 112
    private val REQUEST_CODE_SCAN = 114
    private var btnSize = 0;

    private fun initStatusBar() {

        StatusBarUtil.setColor(this@MainActivity, resources.getColor(R.color.colorPrimary))
    }

    /**
     * 获取权限
     */
    private fun getPermissions(isScan: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                //具有权限

                if (isScan) {
                    toScan()
                } else {
                    toCamera()
                }
            } else { //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), if (isScan) REQUEST_SCAN_PERMISSIOn else REQUEST_CAMERA_PERMISSION
                )

            }
        } else {
            if (isScan) {
                toScan()
            } else {
                toCamera()
            }
        }
    }


    fun toCamera(): Unit {
        ISNav.getInstance().toCamera(this, null, IMG_REQUEST)


    }

    fun toScan(): Unit {
        //TODO 扫描
        IntentIntegrator(this).setBeepEnabled(true).setRequestCode(REQUEST_CODE_SCAN)
            .initiateScan()

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == REQUEST_CAMERA_PERMISSION) {
                toCamera()
            } else {
                toScan()
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_SCAN && resultCode == Activity.RESULT_OK) {

            val result = IntentIntegrator.parseActivityResult(resultCode, data)
            if (result.contents == null) {
            } else {
                ToastUtils.showToast(result.contents)
//                toCamera()
            }
        } else if (requestCode == IMG_REQUEST && resultCode == Activity.RESULT_OK) {

            val listImg = data?.getStringArrayListExtra(CameraActivity.FINISH_KEY)

            ToastUtils.showToast("照片路径 " + (listImg?.get(0) ?: "未获取到照片"))
        }

    }

    override fun getBindingVariable(): Int {

        return BR.mainModel
    }

    override fun getLayoutId(): Int {

        return R.layout.activity_main
    }

    override fun initView() {

        initStatusBar()

        showLeft(false, 0)
        showTile(true, "bondex-photo")

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

        img_btn.setOnClickListener { _ ->

            val items = arrayOf("蓝色（默认）", "黑色", "白色")
            MenuDialogBuilder(this)
                .addItems(items) { dialog, which ->
                    dialog.dismiss()
                }
                .setSkinManager(QMUISkinManager.defaultInstance(this))
                .create()
                .show()
        }
        it_scan.setOnClickListener { _ -> toScan() }


    }


    override fun initListener() {
    }

}


