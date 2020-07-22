package com.bondex.photo.main.first

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bondex.library.base.BaseFragment
import com.bondex.photo.BR

import com.bondex.photo.R
import com.bondex.photo.databinding.FirstFragmentBinding

class FirstFragment : BaseFragment<FirstFragmentBinding, FirstViewModel>() {
    override fun getBindingVariable(): Int {

        return BR.firstModel
    }

    override fun getLayoutId(): Int {

     return   R.layout.first_fragment
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
