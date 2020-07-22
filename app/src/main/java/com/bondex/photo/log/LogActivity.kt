package com.bondex.photo.log

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.bondex.library.base.BaseActivity
import com.bondex.library.util.NoDoubleClickListener
import com.bondex.library.util.ToastUtils
import com.bondex.photo.BR
import com.bondex.photo.R
import com.bondex.photo.databinding.ActivityLogBinding
import com.bondex.photo.utils.itf.ViewModelCallBack
import kotlinx.android.synthetic.main.activity_log.*

class LogActivity : BaseActivity<ActivityLogBinding, LogViewModel>() {


    override fun getBindingVariable(): Int {
//        return BR.logViewModel

        return 0
    }

    override fun getLayoutId(): Int {

        return R.layout.activity_log
    }

    override fun initView() {

        showLeft(true, 0)
        viewModel.isMq = intent.getBooleanExtra("log", true)

        val title: String = if (viewModel.isMq) {
            "查看MQ日志"
        } else {
            "查看上传日志"
        }
        showTile(true, title)

        if (!viewModel.isMq) {

            showRight(true, R.string.upload, object : NoDoubleClickListener() {
                override fun click(v: View?) {

                    viewModel.reUploadPhoto(object : ViewModelCallBack<String> {
                        override fun logBack(str: String) {

                            if (str.equals("null")) {
                                ToastUtils.showToast("未查到上传失败的日志")
                            } else if (str.equals("start")) {

                                viewModel.loading.postValue(true)

                            } else if (str.equals("y")) {

                                viewModel.loading.postValue(false)
                            }

                        }
                    })
                }
            })
            tv_top.visibility = View.VISIBLE
        } else {
            tv_top.visibility = View.GONE

        }


        initRecycler()
    }

    private fun initRecycler() {
        val manager = LinearLayoutManager(this)
        log_recyclerview.layoutManager = manager

        viewModel.readLog(object : ViewModelCallBack<String> {
            override fun logBack(str: String) {

                if (viewModel.isMq) {
                    log_recyclerview.adapter = viewModel.mqAdapter
                } else {
                    log_recyclerview.adapter = viewModel.businessAdapter
                }

            }
        })

    }

    override fun initListener() {
    }

    override fun handleMsg(msg: String?) {

    }

    override fun myClick(v: View?) {

    }
}
