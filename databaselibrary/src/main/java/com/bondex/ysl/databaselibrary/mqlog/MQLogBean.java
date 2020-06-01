package com.bondex.ysl.databaselibrary.mqlog;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * date: 2020/5/22
 *
 * @Author: ysl
 * description:
 */
@Entity
public class MQLogBean {

    @NonNull
    @PrimaryKey
    private long time_id;

    @ColumnInfo(name = "seqno")
    private String SeqNo;

    @ColumnInfo(name = "call_back_type")
    private String CallbackType;
    @ColumnInfo(name = "callback_exchange")
    private String CallbackExchange;
    @ColumnInfo(name = "callback_routing_key")
    private String CallbackRoutingkey;
    @ColumnInfo(name = "action")
    private String Action;
    @ColumnInfo(name = "sender_id")
    private String SenderID;
    @ColumnInfo(name = "sender_name")
    private String SenderName;
    @ColumnInfo(name = "reciver_id")
    private String ReciverID;
    @ColumnInfo(name = "reciver_name")
    private String ReciverName;
    @ColumnInfo(name = "doc_type_id")
    private String DocTypeID;
    @ColumnInfo(name = "doc_type_name")
    private String DocTypeName;
    @ColumnInfo(name = "create_time")
    private String CreateTime;
    @ColumnInfo(name = "finish_time")
    private String FinishTime;
    @ColumnInfo(name = "data_interface_id")
    private String DataInterfaceID;
    @ColumnInfo(name = "data_interface_name")
    private String DataInterfaceName;

    public long getTime_id() {
        return time_id;
    }

    public void setTime_id(long time_id) {
        this.time_id = time_id;
    }

    public String getSeqNo() {
        return SeqNo == null ? "" : SeqNo;
    }

    public void setSeqNo(String seqNo) {
        SeqNo = seqNo == null ? "" : seqNo;
    }

    public String getCallbackType() {
        return CallbackType == null ? "" : CallbackType;
    }

    public void setCallbackType(String callbackType) {
        CallbackType = callbackType == null ? "" : callbackType;
    }

    public String getCallbackExchange() {
        return CallbackExchange == null ? "" : CallbackExchange;
    }

    public void setCallbackExchange(String callbackExchange) {
        CallbackExchange = callbackExchange == null ? "" : callbackExchange;
    }

    public String getCallbackRoutingkey() {
        return CallbackRoutingkey == null ? "" : CallbackRoutingkey;
    }

    public void setCallbackRoutingkey(String callbackRoutingkey) {
        CallbackRoutingkey = callbackRoutingkey == null ? "" : callbackRoutingkey;
    }

    public String getAction() {
        return Action == null ? "" : Action;
    }

    public void setAction(String action) {
        Action = action == null ? "" : action;
    }

    public String getSenderID() {
        return SenderID == null ? "" : SenderID;
    }

    public void setSenderID(String senderID) {
        SenderID = senderID == null ? "" : senderID;
    }

    public String getSenderName() {
        return SenderName == null ? "" : SenderName;
    }

    public void setSenderName(String senderName) {
        SenderName = senderName == null ? "" : senderName;
    }

    public String getReciverID() {
        return ReciverID == null ? "" : ReciverID;
    }

    public void setReciverID(String reciverID) {
        ReciverID = reciverID == null ? "" : reciverID;
    }

    public String getReciverName() {
        return ReciverName == null ? "" : ReciverName;
    }

    public void setReciverName(String reciverName) {
        ReciverName = reciverName == null ? "" : reciverName;
    }

    public String getDocTypeID() {
        return DocTypeID == null ? "" : DocTypeID;
    }

    public void setDocTypeID(String docTypeID) {
        DocTypeID = docTypeID == null ? "" : docTypeID;
    }

    public String getDocTypeName() {
        return DocTypeName == null ? "" : DocTypeName;
    }

    public void setDocTypeName(String docTypeName) {
        DocTypeName = docTypeName == null ? "" : docTypeName;
    }

    public String getCreateTime() {
        return CreateTime == null ? "" : CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime == null ? "" : createTime;
    }

    public String getFinishTime() {
        return FinishTime == null ? "" : FinishTime;
    }

    public void setFinishTime(String finishTime) {
        FinishTime = finishTime == null ? "" : finishTime;
    }

    public String getDataInterfaceID() {
        return DataInterfaceID == null ? "" : DataInterfaceID;
    }

    public void setDataInterfaceID(String dataInterfaceID) {
        DataInterfaceID = dataInterfaceID == null ? "" : dataInterfaceID;
    }

    public String getDataInterfaceName() {
        return DataInterfaceName == null ? "" : DataInterfaceName;
    }

    public void setDataInterfaceName(String dataInterfaceName) {
        DataInterfaceName = dataInterfaceName == null ? "" : dataInterfaceName;
    }
}
