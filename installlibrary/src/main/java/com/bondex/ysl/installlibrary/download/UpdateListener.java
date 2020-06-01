package com.bondex.ysl.installlibrary.download;

/**
 * date: 2020/6/1
 *
 * @Author: ysl
 * description:
 */
public interface UpdateListener {

    void update(UpdateBean updateBean);

    void notUpdate(String msg);
}
