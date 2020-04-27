package com.bondex.photo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import com.bondex.library.util.StatusBarUtil
import com.bondex.photo.databinding.ActivityMainBinding
import com.bondex.ysl.camera.ISNav
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private final val IMG_REQUEST = 1001
    private val REQUEST_PERMISSION = 113

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        initStatusBar()


        btn_camera.setOnClickListener { v ->

            getPermissions()
        }
    }

    private fun initStatusBar() {

        StatusBarUtil.setColor(this@MainActivity, resources.getColor(R.color.colorPrimary))
    }

    /**
     * 获取权限
     */
    private fun getPermissions() {
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
                toCamera()
            } else { //不具有获取权限，需要进行权限申请
                ActivityCompat.requestPermissions(
                    this, arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), REQUEST_PERMISSION
                )
            }
        } else {
            toCamera()
        }
    }


    fun toCamera(): Unit {
        ISNav.getInstance().toCamera(this, null, IMG_REQUEST)


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_PERMISSION) {

            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                toCamera()
            }

        }


    }

}


