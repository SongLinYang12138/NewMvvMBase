package com.bondex.photo.test

import com.bondex.photo.BR
import com.bondex.photo.R
import com.bondex.library.base.BaseActivity
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
        binding.testModel = viewModel
    }

    override fun initListener() {

    }

    override fun handleMsg(msg: String?) {

    }

}
