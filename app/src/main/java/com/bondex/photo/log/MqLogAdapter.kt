package com.bondex.photo.log

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bondex.library.util.CommonUtils
import com.bondex.photo.R
import com.bondex.ysl.databaselibrary.mqlog.MQLogBean

/**
 * date: 2020/5/27
 * @Author: ysl
 * description:
 */
class MqLogAdapter : RecyclerView.Adapter<MqLogAdapter.ViewHolder>() {

    var list: MutableList<MQLogBean> = mutableListOf()
    var context: Context? = null

    fun updateList(list: MutableList<MQLogBean>) {
        this.list = list
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapter_log_mq_layout, parent, false)
        val holder = ViewHolder(view)

        return holder
    }

    override fun getItemCount(): Int {

        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val bean = list.get(position)

        holder.tv?.setText("${bean.senderName + "　->　" + bean.reciverName + "　　" + bean.docTypeName + "　　" + bean.createTime}")


    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var tv: TextView? = null

        init {
            tv = v.findViewById(R.id.mq_tv)
        }

    }

}