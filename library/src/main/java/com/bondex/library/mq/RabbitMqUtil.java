package com.bondex.library.mq;

import android.util.Log;

import com.bondex.library.util.MQCallBack;
import com.bondex.library.util.MQShutodwnListener;
import com.google.gson.Gson;
import com.orhanobut.logger.Logger;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BlockedListener;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ReturnListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.impl.DefaultExceptionHandler;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeoutException;


/**
 * date: 2019/9/11
 * Author: ysl
 * description:
 */
public class RabbitMqUtil {

    // 声明ConnectionFactory对象
    private ConnectionFactory factory = new ConnectionFactory();
    private RabbitMqConfig config;
    private static RabbitMqUtil instance;
    private LinkedBlockingQueue<MqMsgBean> queue = new LinkedBlockingQueue<>();
    private MQCallBack mqCallBack;
    private Connection connection;
    private Channel channel;

    private MQShutodwnListener shutDownCallBack;

    public void setShutDownCallBack(MQShutodwnListener shutDownCallBack) {
        this.shutDownCallBack = shutDownCallBack;
    }

    private ShutdownListener connectShutDownListener = new ShutdownListener() {
        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {

            if (shutDownCallBack != null) {
                shutDownCallBack.shutDown(cause.toString());
            }

            Log.e("aaa", "mq connect shutdown " + cause.toString());
        }
    };

    private ShutdownListener channelShutdownListener = new ShutdownListener() {
        @Override
        public void shutdownCompleted(ShutdownSignalException cause) {

            if (shutDownCallBack != null) {
                shutDownCallBack.shutDown(cause.toString());
            }
            Log.e("aaa", "mq channel shutdown " + cause.toString());


        }
    };
    private BlockedListener blockedListener = new BlockedListener() {
        @Override
        public void handleBlocked(String reason) throws IOException {

            Logger.i("aaa" + "blcked  " + reason);
        }

        @Override
        public void handleUnblocked() throws IOException {
            Logger.i("aaa " + "unblcked  ");

        }
    };

    private ReturnListener returnListener = new ReturnListener() {
        @Override
        public void handleReturn(int replyCode, String replyText, String exchange, String routingKey, AMQP.BasicProperties properties, byte[] body) throws IOException {


            Logger.i("returnListener " + replyCode + "  " + replyText + "  " + exchange + "  " + routingKey);

        }
    };

    /**
     * 连接rabbitmq
     */


    public RabbitMqUtil(RabbitMqConfig config) {

        this.config = config;
        setUpConnectionFactory();

    }


    public RabbitMqUtil setMqMsgBean(MqMsgBean mqMsgBean) {

        queue.add(mqMsgBean);

        return this;
    }

    public String getMsg(MqMsgBean mqMsgBean) {

        Gson gson = new Gson();

        String msg = gson.toJson(mqMsgBean);


        Log.i("aaa", "mqJson " + msg);
        return msg;
    }


    public void setMqCallBack(MQCallBack mqCallBack) {

        this.mqCallBack = mqCallBack;
    }

    /**
     * 连接设置
     */
    private void setUpConnectionFactory() {
//主机地址：192.168.1.105
        factory.setHost(config.getHost());
        // 端口号:5672
        factory.setPort(config.getPort());
        // 用户名
        factory.setUsername(config.getUsrname());
        // 密码
        factory.setPassword(config.getPassword());
        factory.setRequestedHeartbeat(20000);
        factory.setConnectionTimeout(60000);
        // 设置连接恢复
        factory.setAutomaticRecoveryEnabled(true);

        factory.setExceptionHandler(new DefaultExceptionHandler() {
            @Override
            public void handleConfirmListenerException(Channel channel, Throwable exception) {
                super.handleConfirmListenerException(channel, exception);

                Log.e("aaa", "handelException " + exception.toString());
            }
        });
    }

    public boolean isConnected() {

        if (connection == null || !connection.isOpen()) {
            return false;
        }

        if (channel == null || !channel.isOpen()) {
            return false;
        }

        return true;
    }


    public void startMQ() {

        try {

            if (connection == null || !connection.isOpen() || !channel.isOpen()) {
                // 创建连接
                if (connection == null || !connection.isOpen()) {
                    connection = factory.newConnection();
                }
                // 创建通道
                if (channel == null || !channel.isOpen()) {
                    channel = connection.createChannel();
                }

//                channel.confirmSelect();
                connection.addShutdownListener(connectShutDownListener);
                connection.addBlockedListener(blockedListener);
                channel.addShutdownListener(channelShutdownListener);
                channel.addReturnListener(returnListener);


                /**
                 * 创建一个type=direct 持久化的 非自动删除的交换器
                 */
                channel.exchangeDeclare(config.getExchange(), "direct", true, false, null);
                /**
                 * 创建一个持久化 百排他的 非自动删除的队列
                 */
                channel.queueDeclare(config.getQueue_name(), true, false, false, null);


                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                        super.handleDelivery(consumerTag, envelope, properties, body);


                        String message = new String(body, "UTF-8");

                        if (mqCallBack != null) {
                            mqCallBack.sendMsg(message);
                        }

                        Log.i("aaa", "mq RECEIVE'" + envelope.getRoutingKey() + "':'" + message + "'");
                    }
                };

                channel.basicConsume(config.getQueue_name(), true, consumer);
            }

            ////                    // 绑定队列 exchange rote_key

            channel.queueBind(config.getQueue_name(), config.getExchange(), config.getRoute_key());
//                    while (!queue.isEmpty()) {
//                        channel.basicPublish(config.getExchange(), config.getRoute_key(), MessageProperties.PERSISTENT_TEXT_PLAIN, getMsg(queue.poll()).getBytes());
//                    }

            if (mqCallBack != null) {
                mqCallBack.startSuccess();
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (mqCallBack != null) {
                mqCallBack.startFail(e.getMessage());
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
            if (mqCallBack != null) {
                mqCallBack.startFail(e.getMessage());
            }
        }
    }


}
