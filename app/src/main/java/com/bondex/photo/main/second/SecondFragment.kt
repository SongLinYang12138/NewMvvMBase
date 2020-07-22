package com.bondex.photo.main.second

import android.view.View
import com.bondex.library.base.BaseFragment
import com.bondex.photo.BR

import com.bondex.photo.R
import com.bondex.photo.databinding.SecondFragmentBinding

class SecondFragment :BaseFragment<SecondFragmentBinding,SecondViewModel>() {
    override fun getBindingVariable(): Int {

        return BR.secondModel

    }

    override fun getLayoutId(): Int {
        return R.layout.second_fragment
    }

    override fun initView() {
    }

    override fun initListener() {
    }

    override fun handleMsg(msg: String?) {
    }

    override fun myClick(v: View?) {
    }

}
