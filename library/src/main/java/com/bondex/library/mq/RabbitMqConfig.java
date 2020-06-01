package com.bondex.library.mq;

import android.util.Log;

import java.io.Serializable;

/**
 * date: 2019/9/11
 * Author: ysl
 * description:
 */
public class RabbitMqConfig implements Serializable {


    private String host;
    private int port;
    private String usrname;
    private String password;
    private String queue_name;
    private String exchange;
    private String route_key;

    public String getHost() {
        return host == null ? "" : host;
    }

    public int getPort() {
        return port;
    }

    public String getUsrname() {
        return usrname == null ? "" : usrname;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public String getQueue_name() {
        return queue_name == null ? "" : queue_name;
    }

    public String getExchange() {
        return exchange == null ? "" : exchange;
    }

    public String getRoute_key() {
        return route_key == null ? "" : route_key;
    }

    public RabbitMqConfig(Builder builder) {

        this.host = builder.host;
        this.port = builder.port;
        this.usrname = builder.usrname;
        this.password = builder.password;
        this.queue_name = builder.queue_name;
        this.exchange = builder.exchange;
        this.route_key = builder.route_key;

    }


    public static class Builder implements Serializable {

        private String host = "mq.bondex.com.cn";
        private int port = 5672;
        private String usrname = "ZXOSRMQ054";
        private String password = "astiasti";
        private String queue_name;
        private String exchange;
        private String route_key;

        public Builder(String clientMqGuid) {

            queue_name = "Bondex.CameraSystemClient"+clientMqGuid+".ListenQueue";
            exchange = "Bondex.CameraSystemClient"+clientMqGuid+".ListenExchange";
            route_key = "Bondex.CameraSystemClient"+clientMqGuid+".RoutingKey";

//            queue_name = "Bondex.CameraSystemClient.ListenQueue";
//            exchange = "Bondex.CameraSystemClient.ListenExchange";
//            route_key = "Bondex.CameraSystemClient.RoutingKey";
            Log.i("aaa","queue "+queue_name+"\nexchange "+exchange+"\nroute_key "+route_key);
        }

        public RabbitMqConfig build() {

            return new RabbitMqConfig(this);
        }
    }


}
