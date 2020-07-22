package com.bondex.photo.main

import android.app.Dialog
import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.bondex.library.base.BaseViewMode
import com.bondex.library.util.CommonUtils
import com.bondex.library.util.ToastUtils
import com.bondex.photo.R
import com.bondex.photo.main.first.FirstFragment
import com.bondex.photo.main.second.SecondFragment
import com.bondex.photo.utils.MD5
import com.bondex.ysl.installlibrary.InstallApk
import com.bondex.ysl.installlibrary.download.DownloadListener
import com.bondex.ysl.installlibrary.download.UpdateBean
import com.orhanobut.logger.Logger
import org.json.JSONException
import org.json.JSONObject

/**
 * date: 2020/5/20
 * @Author: ysl
 * description:
 */
class MainViewModel : BaseViewMode<MainModel>(), MainCallBack {

    private val firstFrament = FirstFragment()
    private val secondFragment = SecondFragment()
    private var fragmentManager: FragmentManager? = null
    private var transaction: FragmentTransaction? = null;

    internal fun createManager(manager: FragmentManager, id: Int) {
        fragmentManager = manager;
        transaction = fragmentManager?.beginTransaction();
        transaction?.add(id, firstFrament, "first")
        transaction?.add(id, secondFragment, "second")
        transaction?.hide(secondFragment)
        transaction?.commit()

    }

    internal fun switchFragment(flag: Int) {

        transaction = fragmentManager?.beginTransaction();

        transaction?.let {

            when (flag) {
                0 -> {
                    if (firstFrament.isHidden) {
                        it.hide(secondFragment)
                        it.show(firstFrament)
                    }
                }
                1 -> {
                    if (secondFragment.isHidden) {
                        it.hide(firstFrament)
                        it.show(secondFragment)
                    }
                }
                else -> {
                }
            }
            it.commit()
        }
    }



    override fun onResume() {

    }

    override fun onStop() {
    }

    override fun onCreate() {


    }

    override fun attachUi() {
    }

    override fun detachUi() {
    }


    override fun onStart() {
    }

    override fun onDestroy() {
    }


    override fun setMyModel() {

        this.model = MainModel(this)

    }

}