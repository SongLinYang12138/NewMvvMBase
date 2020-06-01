package com.bondex.photo.bean;

/**
 * date: 2020/5/25
 *
 * @Author: ysl
 * description:
 */
public class UploadResultBean {


    /**
     * Message : 文件上传成功.
     * Data : 1589873300860.png
     * Code : 0
     * ExecutionTime : 2020-05-19 15:28:20
     * Success : true
     */

    private String Message;
    private String Data;
    private int Code;
    private String ExecutionTime;
    private boolean Success;

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getData() {
        return Data;
    }

    public void setData(String Data) {
        this.Data = Data;
    }

    public int getCode() {
        return Code;
    }

    public void setCode(int Code) {
        this.Code = Code;
    }

    public String getExecutionTime() {
        return ExecutionTime;
    }

    public void setExecutionTime(String ExecutionTime) {
        this.ExecutionTime = ExecutionTime;
    }

    public boolean isSuccess() {
        return Success;
    }

    public void setSuccess(boolean Success) {
        this.Success = Success;
    }
}
