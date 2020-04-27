package com.bondex.photo.test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.bondex.photo.BR
import com.bondex.photo.R
import com.bondex.photo.base.BaseActivity
import com.bondex.photo.databinding.ActivityTestBinding

class TestActivity : BaseActivity<ActivityTestBinding, TestViewModel>() {
    override fun getBindingVariable(): Int {

        return BR.testModel
    }

    override fun getLayoutId(): Int {

        return R.layout.activity_test
    }

    override fun initView() {

        val editObserver = EditObserver()

        binding.editobserver = editObserver

    }

    override fun initListener() {

    }

}
