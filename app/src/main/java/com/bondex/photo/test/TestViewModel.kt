package com.bondex.photo.test

import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.ObservableField
import com.bondex.library.util.CommonUtils
import com.bondex.photo.app.PhotoApplication
import com.bondex.photo.base.BaseViewMode


/**
 * date: 2020/4/26
 * @Author: ysl
 * description:
 */
class TestViewModel : BaseViewMode() {
    val tv = ObservableField<String>()


    fun onSaveClick(view: View?) {

        Toast.makeText(PhotoApplication.context, "点击", Toast.LENGTH_SHORT).show()
    }




    override fun onResume() {

    }

    override fun onStop() {
    }

    override fun onCreate() {

        tv.set("mytv1")
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