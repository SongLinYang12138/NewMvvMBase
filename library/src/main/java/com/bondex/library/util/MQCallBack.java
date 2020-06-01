package com.bondex.library.util;

/**
 * date: 2020/5/20
 *
 * @Author: ysl
 * description:
 */
public interface MQCallBack {

    void sendMsg(String mq);

    void startSuccess();

    void startFail(String error);
}
