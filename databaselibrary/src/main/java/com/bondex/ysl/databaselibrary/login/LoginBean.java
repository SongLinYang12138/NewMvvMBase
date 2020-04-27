package com.bondex.ysl.databaselibrary.login;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * date: 2019/7/19
 * Author: ysl
 * description:
 */
//    {"success":true,"message":[
//            {"psncode":"0007700",
//                    "psnname":"杨松林",
//                    "email":"yangsonglin@bondex.com.cn",
//                    "vdef1":"bondex.com.cn",
//                    "token":"BAF9A0A6696361106209C1A754B5E687B37529E982D96AEA37BA2483E9AEC19E04DD7DD228A297657C327D08022D8733C4E897987F2D73CF209C1B4CBFC9C4965BC1D1233F0266DF8EAF29FDABFC9B76B5DB91B67B00F2CA3F175138452A757C1805E08962D3FD5FF2D613BCAD3EA1F036E63C2480BE33FBC9CC8A298147AFA570C1014260F9811C6AA5AB6C66E138BF4D7874618CCB9446B2F554330584A5315921B643A71D9CF6",
//                    "cstoken":"E17AB0B2278B8AEC377DE3C72DD266D0864B47F11D3F02E986833A97E9C4CF5504B765E24A9B7EA5",
//                    "opids":{"IT051":"成都IT/杨松林"}
//            }]}
//
@Entity
public class LoginBean {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "psncode")
    private String psncode;
    @ColumnInfo(name = "psnname")
    private String psnname;
    @ColumnInfo(name = "psw")
    private String psw;
    @ColumnInfo(name = "email")
    private String email;
    @ColumnInfo(name = "vdef1")
    private String vdef1;
    @ColumnInfo(name = "token")
    private String token;
    @ColumnInfo(name = "cstoken")
    private String cstoken;
    @ColumnInfo(name = "opid")
    private String opid;
    @ColumnInfo(name = "opidName")
    private String opidName;
    @ColumnInfo(name = "isLogin")
    private boolean isLogin;
    @ColumnInfo(name = "loginDate")
    private long loginDate;
    @ColumnInfo(name = "shouldRember")
    private boolean shouldRember;


    public boolean isShouldRember() {
        return shouldRember;
    }

    public void setShouldRember(boolean shouldRember) {
        this.shouldRember = shouldRember;
    }

    public String getPsw() {
        return psw == null ? "" : psw;
    }

    public void setPsw(String psw) {
        this.psw = psw == null ? "" : psw;
    }

    public long getLoginDate() {
        return loginDate;
    }

    public void setLoginDate(long loginDate) {
        this.loginDate = loginDate;
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setLogin(boolean login) {
        isLogin = login;
    }

    public String getOpid() {
        return opid == null ? "" : opid;
    }

    public void setOpid(String opid) {
        this.opid = opid == null ? "" : opid;
    }

    public String getOpidName() {
        return opidName == null ? "" : opidName;
    }

    public void setOpidName(String opidName) {
        this.opidName = opidName == null ? "" : opidName;
    }

    //
    public String getPsncode() {
        return psncode;
    }

    public void setPsncode(String psncode) {
        this.psncode = psncode;
    }

    public String getPsnname() {
        return psnname;
    }

    public void setPsnname(String psnname) {
        this.psnname = psnname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVdef1() {
        return vdef1;
    }

    public void setVdef1(String vdef1) {
        this.vdef1 = vdef1;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCstoken() {
        return cstoken;
    }

    public void setCstoken(String cstoken) {
        this.cstoken = cstoken;
    }
}
