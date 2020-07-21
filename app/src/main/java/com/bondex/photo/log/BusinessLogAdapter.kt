package com.bondex.photo.log

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.bondex.library.base.Constant
import com.bondex.library.ui.IconText
import com.bondex.library.util.CommonUtils
import com.bondex.library.util.Tools
import com.bondex.photo.R
import com.bondex.photo.utils.RecyclerListener
import com.bondex.ysl.databaselibrary.buinesslog.BusinessLogBean
import com.bumptech.glide.Glide

import java.io.File

/**
 * date: 2020/5/27
 * @Author: ysl
 * description:
 */
class BusinessLogAdapter : RecyclerView.Adapter<BusinessLogAdapter.ViewHolder>() {


    var list: MutableList<BusinessLogBean> = mutableListOf()

    var context: Context? = null

    var dialog: Dialog? = null

    var listener: RecyclerListener<BusinessLogBean>? = null

    fun updateList(list: MutableList<BusinessLogBean>) {
        this.list = list
        notifyDataSetChanged()

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view = LayoutInflater.from(context)
            .inflate(R.layout.adapter_log_business_layout, parent, false)

        val holder = ViewHolder(view)

        holder.tv?.setOnClickListener { v ->

            var position = v?.getTag() as Int
        }
        return holder
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val bean = list.get(position)
        val status = if (bean.status != 0) {

            "提交失败"
        } else {
            "成功"
        }
        holder.tv?.setTag(position)
        holder.tv?.setText(
            Tools.getDateAndTime(bean.create_time)
                    + "　　" + bean.content + if (CommonUtils.isNotEmpty(bean.qrcode)) "　　" + bean.qrcode + "　　" + bean.filePath else "" + "　　" + bean.filePath
        )

    }



    var dialog_iv: ImageView? = null

    private fun showImgDialog(file_path: String) {


        if (dialog == null) {

            dialog = Dialog(context!!, R.style.dialog)
            val view = LayoutInflater.from(context).inflate(R.layout.dialog_img_layout, null, false)
            dialog?.setContentView(view)
            dialog_iv = view.findViewById(R.id.dialog_iv)

            dialog_iv?.setMaxHeight(CommonUtils.getScreenH(context))

//            Glide.with(context).load(getFileUri()).into(dialog_iv)


            dialog?.setCanceledOnTouchOutside(true)
            val dialog_close: IconText = view.findViewById(R.id.dialog_close)

            dialog_close.setOnClickListener { dialog?.dismiss() }

            val window = dialog?.window?.attributes
            window?.width = CommonUtils.getScreenW(context) - 100
            window?.height = CommonUtils.getScreenH(context) * 3 / 4
            window?.gravity = Gravity.CENTER

            dialog?.window?.attributes = window

            dialog?.show()
        } else {

            if (dialog?.isShowing!!) {
                dialog?.dismiss()
            } else {
//                Glide.with(context).load(getFileUri()).into(dialog_iv)
                dialog?.show()
            }

        }


    }


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var tv: TextView? = null

        init {
            tv = itemView.findViewById(R.id.business_tv)
        }


    }

//    private fun getFileUri(): Uri {
//
//        var uri: Uri? = null
//        val file = File(currentFilePath)
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            uri = Uri.fromFile(file)
//        } else {
//            uri = FileProvider.getUriForFile(context!!, Constant.AUTHORITY, file)
//        }
//
//
//        return uri
//    }


}