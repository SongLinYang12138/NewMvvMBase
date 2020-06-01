package com.bondex.library.mq;

/**
 * date: 2019/9/12
 * Author: ysl
 * description:
 */
public class MqMsgBean {

    private String LogId = "0";
    private String AppID = "1013";
    private String AppName = "BondexApp";
    private String ModuleCode;
    private String ModuleName;
    private String LogType;
    private String LogTime;
    private String BillNo = "";
    private String OptsCode;
    private String OptsName;
    private String OptsContent = "";
    private String Message = "";
    private String Interface = "";
    private String VerCode;
    private String HostIP;
    private String ClientIP = "";
    private String OpId = "";
    private String OpName = "";
    private String Token = "";
    private String Remark = "";


    public void setLogId(String logId) {
        LogId = logId == null ? "" : logId;
    }

    public void setAppID(String appID) {
        AppID = appID == null ? "" : appID;
    }

    public void setAppName(String appName) {
        AppName = appName == null ? "" : appName;
    }

    public void setModuleCode(String moduleCode) {
        ModuleCode = moduleCode == null ? "" : moduleCode;
    }

    public void setModuleName(String moduleName) {
        ModuleName = moduleName == null ? "" : moduleName;
    }

    public void setLogType(String logType) {
        LogType = logType == null ? "" : logType;
    }

    public void setLogTime(String logTime) {
        LogTime = logTime == null ? "" : logTime;
    }

    public void setBillNo(String billNo) {
        BillNo = billNo == null ? "" : billNo;
    }

    public void setOptsCode(String optsCode) {
        OptsCode = optsCode == null ? "" : optsCode;
    }

    public void setOptsName(String optsName) {
        OptsName = optsName == null ? "" : optsName;
    }

    public void setOptsContent(String optsContent) {
        OptsContent = optsContent == null ? "" : optsContent;
    }

    public void setMessage(String message) {
        Message = message == null ? "" : message;
    }

    public void setInterface(String anInterface) {
        Interface = anInterface == null ? "" : anInterface;
    }

    public void setVerCode(String verCode) {
        VerCode = verCode == null ? "" : verCode;
    }

    public void setHostIP(String hostIP) {
        HostIP = hostIP == null ? "" : hostIP;
    }

    public void setClientIP(String clientIP) {
        ClientIP = clientIP == null ? "" : clientIP;
    }

    public void setOpId(String opId) {
        OpId = opId == null ? "" : opId;
    }

    public void setOpName(String opName) {
        OpName = opName == null ? "" : opName;
    }

    public void setToken(String token) {
        Token = token == null ? "" : token;
    }

    public void setRemark(String remark) {
        Remark = remark == null ? "" : remark;
    }

    public String getLogId() {
        return LogId == null ? "" : LogId;
    }

    public String getAppID() {
        return AppID == null ? "" : AppID;
    }

    public String getAppName() {
        return AppName == null ? "" : AppName;
    }

    public String getModuleCode() {
        return ModuleCode == null ? "" : ModuleCode;
    }

    public String getModuleName() {
        return ModuleName == null ? "" : ModuleName;
    }

    public String getLogType() {
        return LogType == null ? "" : LogType;
    }

    public String getLogTime() {
        return LogTime == null ? "" : LogTime;
    }

    public String getBillNo() {
        return BillNo == null ? "" : BillNo;
    }

    public String getOptsCode() {
        return OptsCode == null ? "" : OptsCode;
    }

    public String getOptsName() {
        return OptsName == null ? "" : OptsName;
    }

    public String getOptsContent() {
        return OptsContent == null ? "" : OptsContent;
    }

    public String getMessage() {
        return Message == null ? "" : Message;
    }

    public String getInterface() {
        return Interface == null ? "" : Interface;
    }

    public String getVerCode() {
        return VerCode == null ? "" : VerCode;
    }

    public String getHostIP() {
        return HostIP == null ? "" : HostIP;
    }

    public String getClientIP() {
        return ClientIP == null ? "" : ClientIP;
    }

    public String getOpId() {
        return OpId == null ? "" : OpId;
    }

    public String getOpName() {
        return OpName == null ? "" : OpName;
    }

    public String getToken() {
        return Token == null ? "" : Token;
    }

    public String getRemark() {
        return Remark == null ? "" : Remark;
    }
}
