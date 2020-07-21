package com.bondex.photo.mq;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bondex.library.app.PhotoApplication;
import com.bondex.library.mq.MqMsgBean;
import com.bondex.library.util.CommonUtils;
import com.bondex.library.util.MQCallBack;
import com.bondex.library.util.MQShutodwnListener;
import com.bondex.ysl.databaselibrary.mqlog.MQLogBeanDao;
import com.bondex.ysl.databaselibrary.mqlog.MQLogBeanDatabase;

import org.jetbrains.annotations.NotNull;

/**
 * date: 2020/5/20
 *
 * @Author: ysl
 * description:
 */
public class MQManager {

    private MQRunnable mqWork;

    private static MQManager mqManager;

    public static final int TAKE_PHOTO = 101;
    public static final int START_SUCCESS = 111;
    public static final int START_FAIL = 011;
    public static final int SHUTDOWN = 0;


    private Handler resultHandler;


    private MQCallBack listener = new MQCallBack() {
        @Override
        public void sendMsg(String mq) {
            Log.i("aaa", "sendMsg " + mq);

            if (resultHandler != null) {

                Message msg = new Message();
                msg.what = TAKE_PHOTO;
                msg.obj = mq;

                resultHandler.sendMessage(msg);
            }
        }

        @Override
        public void startSuccess() {

            Log.i("aaa", "mq start success");

            if (resultHandler != null) {
                resultHandler.sendEmptyMessage(START_SUCCESS);
            }
        }

        @Override
        public void startFail(String error) {
            Log.i("aaa", "mq start faile");

            if (resultHandler != null) {
                Message msg = new Message();
                msg.what = START_FAIL;
                msg.obj = error;

                resultHandler.sendMessage(msg);
            }
        }
    };
    private MQShutodwnListener mqShutdownListener = new MQShutodwnListener() {
        @Override
        public void shutDown(@NotNull String error) {
            Log.i("aaa", "errorListener " + error);

            if (resultHandler != null) {

                Message msg = new Message();
                msg.what = SHUTDOWN;
                msg.obj = error;
                resultHandler.sendMessage(msg);
            }

        }
    };

    public static MQManager getInstance(String clientMqGuid) {

        if (clientMqGuid == null) {
            clientMqGuid = "";
        }

        if (mqManager == null) {

            mqManager = new MQManager(clientMqGuid);

        } else if (CommonUtils.isNotEmpty(clientMqGuid)) {
            mqManager.updateClientMqGuid(clientMqGuid);
        }

        return mqManager;
    }

    private MQManager(String clientMqGuid) {

        mqWork = new MQRunnable(clientMqGuid);
        mqWork.setListener(listener);
        mqWork.setMqShutodwn(mqShutdownListener);

    }

    private void updateClientMqGuid(String clientMqGuid) {

        mqWork.updateClientMqGuid(clientMqGuid);
    }


    public MQManager setResultHandler(Handler resultHandler) {
        this.resultHandler = resultHandler;
        return this;
    }


    public void doWork() {

        Thread thread = new Thread(mqWork);
        thread.start();
    }

    public boolean isConnected() {

        return mqWork.mqConnected();
    }

    public void checkMQ() {

        Log.i("aaa", "check " + mqWork.mqConnected());
        if (!mqWork.mqConnected()) {
//            doWork();
        }

    }

}
