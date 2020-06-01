package com.bondex.library.mq;

import android.os.Parcel;
import android.os.Parcelable;

import com.bondex.library.util.Tools;
import com.bondex.ysl.databaselibrary.mqlog.MQLogBean;

import java.util.Date;

/**
 * date: 2020/5/19
 *
 * @Author: ysl
 * description:
 */
public class MQBean implements Parcelable {


    /**
     * Head : {"SeqNo":"c0abd529ffc84436abe9c109e9e84bca637254772098073927","CallbackType":"Synchronize","CallbackExchange":"Bondex.CameraSystemService.ListenExchange","CallbackRoutingkey":null,"Action":"PhotoGraph","SenderID":"CameraSystem.Service","SenderName":"摄像记录系统","ReciverID":"CameraSystem.Service","ReciverName":"摄像记录系统","DocTypeID":"","DocTypeName":"拍照","CreateTime":"2020-05-19T09:26:49.8073927+08:00","FinishTime":"2020-05-20T09:26:49.8073927+08:00","DataInterfaceID":null,"DataInterfaceName":"执行命令","Version":"1.0","Token":null}
     * Main : {"Data":null,"TransmissionUser":null,"TransmissionTime":"2020-05-19T09:26:49.8073927+08:00","ClientIpAddress":null}
     */


    private HeadBean Head;
    private MainBean Main;

    protected MQBean(Parcel in) {


    }

    public MQLogBean toMqLogBean() {

        MQLogBean bean = new MQLogBean();

        bean.setTime_id(System.currentTimeMillis());
        bean.setAction(getHead().getAction());
        bean.setCallbackExchange(getHead().getCallbackExchange());
        bean.setCallbackRoutingkey(getHead().getCallbackRoutingkey());
        bean.setCallbackType(getHead().getCallbackType());
        bean.setCreateTime(getHead().getCreateTime());
        bean.setDataInterfaceID(getHead().getDataInterfaceID());
        bean.setDataInterfaceName(getHead().getDataInterfaceName());
        bean.setDocTypeID(getHead().getDocTypeID());
        bean.setDocTypeName(getHead().getDocTypeName());
        bean.setFinishTime(getHead().getFinishTime());
        bean.setReciverID(getHead().getReciverID());
        bean.setReciverName(getHead().getReciverName());
        bean.setSenderID(getHead().getSenderID());
        bean.setSenderName(getHead().getSenderName());
        bean.setSeqNo(getHead().getSeqNo());
        return bean;
    }


    public static final Creator<MQBean> CREATOR = new Creator<MQBean>() {
        @Override
        public MQBean createFromParcel(Parcel in) {
            return new MQBean(in);
        }

        @Override
        public MQBean[] newArray(int size) {
            return new MQBean[size];
        }
    };

    public HeadBean getHead() {
        return Head;
    }

    public void setHead(HeadBean Head) {
        this.Head = Head;
    }

    public MainBean getMain() {
        return Main;
    }

    public void setMain(MainBean Main) {
        this.Main = Main;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    public static class HeadBean implements Parcelable {
        /**
         * SeqNo : c0abd529ffc84436abe9c109e9e84bca637254772098073927
         * CallbackType : Synchronize
         * CallbackExchange : Bondex.CameraSystemService.ListenExchange
         * CallbackRoutingkey : null
         * Action : PhotoGraph
         * SenderID : CameraSystem.Service
         * SenderName : 摄像记录系统
         * ReciverID : CameraSystem.Service
         * ReciverName : 摄像记录系统
         * DocTypeID :
         * DocTypeName : 拍照
         * CreateTime : 2020-05-19T09:26:49.8073927+08:00
         * FinishTime : 2020-05-20T09:26:49.8073927+08:00
         * DataInterfaceID : null
         * DataInterfaceName : 执行命令
         * Version : 1.0
         * Token : null
         */

        private String SeqNo;
        private String CallbackType;
        private String CallbackExchange;
        private String CallbackRoutingkey;
        private String Action;
        private String SenderID;
        private String SenderName;
        private String ReciverID;
        private String ReciverName;
        private String DocTypeID;
        private String DocTypeName;
        private String CreateTime;
        private String FinishTime;
        private String DataInterfaceID;
        private String DataInterfaceName;
        private String Version;
        private String Token;

        protected HeadBean(Parcel in) {
            SeqNo = in.readString();
            CallbackType = in.readString();
            CallbackExchange = in.readString();
            CallbackRoutingkey = in.readString();
            Action = in.readString();
            SenderID = in.readString();
            SenderName = in.readString();
            ReciverID = in.readString();
            ReciverName = in.readString();
            DocTypeID = in.readString();
            DocTypeName = in.readString();
            CreateTime = in.readString();
            FinishTime = in.readString();
            DataInterfaceID = in.readString();
            DataInterfaceName = in.readString();
            Version = in.readString();
            Token = in.readString();
        }

        public static final Creator<HeadBean> CREATOR = new Creator<HeadBean>() {
            @Override
            public HeadBean createFromParcel(Parcel in) {
                return new HeadBean(in);
            }

            @Override
            public HeadBean[] newArray(int size) {
                return new HeadBean[size];
            }
        };

        public String getSeqNo() {
            return SeqNo;
        }

        public void setSeqNo(String SeqNo) {
            this.SeqNo = SeqNo;
        }

        public String getCallbackType() {
            return CallbackType;
        }

        public void setCallbackType(String CallbackType) {
            this.CallbackType = CallbackType;
        }

        public String getCallbackExchange() {
            return CallbackExchange;
        }

        public void setCallbackExchange(String CallbackExchange) {
            this.CallbackExchange = CallbackExchange;
        }

        public String getCallbackRoutingkey() {
            return CallbackRoutingkey;
        }

        public void setCallbackRoutingkey(String CallbackRoutingkey) {
            this.CallbackRoutingkey = CallbackRoutingkey;
        }


        public String getAction() {
            return Action;
        }

        public void setAction(String Action) {
            this.Action = Action;
        }

        public String getSenderID() {
            return SenderID;
        }

        public void setSenderID(String SenderID) {
            this.SenderID = SenderID;
        }

        public String getSenderName() {
            return SenderName;
        }

        public void setSenderName(String SenderName) {
            this.SenderName = SenderName;
        }

        public String getReciverID() {
            return ReciverID;
        }

        public void setReciverID(String ReciverID) {
            this.ReciverID = ReciverID;
        }

        public String getReciverName() {
            return ReciverName;
        }

        public void setReciverName(String ReciverName) {
            this.ReciverName = ReciverName;
        }

        public String getDocTypeID() {
            return DocTypeID;
        }

        public void setDocTypeID(String DocTypeID) {
            this.DocTypeID = DocTypeID;
        }

        public String getDocTypeName() {
            return DocTypeName;
        }

        public void setDocTypeName(String DocTypeName) {
            this.DocTypeName = DocTypeName;
        }

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getFinishTime() {
            return FinishTime;
        }

        public void setFinishTime(String FinishTime) {
            this.FinishTime = FinishTime;
        }

        public String getDataInterfaceID() {
            return DataInterfaceID;
        }

        public void setDataInterfaceID(String DataInterfaceID) {
            this.DataInterfaceID = DataInterfaceID;
        }

        public String getDataInterfaceName() {
            return DataInterfaceName;
        }

        public void setDataInterfaceName(String DataInterfaceName) {
            this.DataInterfaceName = DataInterfaceName;
        }

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
            this.Version = Version;
        }

        public String getToken() {
            return Token;
        }

        public void setToken(String Token) {
            this.Token = Token;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(SeqNo);
            dest.writeString(CallbackType);
            dest.writeString(CallbackExchange);
            dest.writeString(CallbackRoutingkey);
            dest.writeString(Action);
            dest.writeString(SenderID);
            dest.writeString(SenderName);
            dest.writeString(ReciverID);
            dest.writeString(ReciverName);
            dest.writeString(DocTypeID);
            dest.writeString(DocTypeName);
            dest.writeString(CreateTime);
            dest.writeString(FinishTime);
            dest.writeString(DataInterfaceID);
            dest.writeString(DataInterfaceName);
            dest.writeString(Version);
            dest.writeString(Token);
        }
    }

    public static class MainBean implements Parcelable {
        /**
         * Data : null
         * TransmissionUser : null
         * TransmissionTime : 2020-05-19T09:26:49.8073927+08:00
         * ClientIpAddress : null
         */

        private String Data;
        private String TransmissionUser;
        private String TransmissionTime;
        private String ClientIpAddress;

        protected MainBean(Parcel in) {
            Data = in.readString();
            TransmissionUser = in.readString();
            TransmissionTime = in.readString();
            ClientIpAddress = in.readString();
        }

        public static final Creator<MainBean> CREATOR = new Creator<MainBean>() {
            @Override
            public MainBean createFromParcel(Parcel in) {
                return new MainBean(in);
            }

            @Override
            public MainBean[] newArray(int size) {
                return new MainBean[size];
            }
        };

        public String getData() {
            return Data;
        }

        public void setData(String Data) {
            this.Data = Data;
        }

        public String getTransmissionUser() {
            return TransmissionUser;
        }

        public void setTransmissionUser(String TransmissionUser) {
            this.TransmissionUser = TransmissionUser;
        }

        public String getTransmissionTime() {
            return TransmissionTime;
        }

        public void setTransmissionTime(String TransmissionTime) {
            this.TransmissionTime = TransmissionTime;
        }

        public String getClientIpAddress() {
            return ClientIpAddress;
        }

        public void setClientIpAddress(String ClientIpAddress) {
            this.ClientIpAddress = ClientIpAddress;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(Data);
            dest.writeString(TransmissionUser);
            dest.writeString(TransmissionTime);
            dest.writeString(ClientIpAddress);
        }
    }
}
