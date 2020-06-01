package com.bondex.photo.bean;

/**
 * date: 2020/5/21
 *
 * @Author: ysl
 * description:
 */
public class QRCodeBean {
//{"ServiceIpAddress":"","ServiceIpPort":"","ClientMQGuid":""}
    private String ServiceIpAddress;
    private String ServiceIpPort;
    private String ClientMQGuid;


    public String getServiceIpAddress() {
        return ServiceIpAddress == null ? "" : ServiceIpAddress;
    }

    public void setServiceIpAddress(String serviceIpAddress) {
        ServiceIpAddress = serviceIpAddress == null ? "" : serviceIpAddress;
    }

    public String getServiceIpPort() {
        return ServiceIpPort == null ? "" : ServiceIpPort;
    }

    public void setServiceIpPort(String serviceIpPort) {
        ServiceIpPort = serviceIpPort == null ? "" : serviceIpPort;
    }

    public String getClientMQGuid() {
        return ClientMQGuid == null ? "" : ClientMQGuid;
    }

    public void setClientMQGuid(String clientMQGuid) {
        ClientMQGuid = clientMQGuid == null ? "" : clientMQGuid;
    }
}
