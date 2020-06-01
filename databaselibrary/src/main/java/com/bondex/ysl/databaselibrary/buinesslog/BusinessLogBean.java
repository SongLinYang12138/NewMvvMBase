package com.bondex.ysl.databaselibrary.buinesslog;

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
public class BusinessLogBean {
    @NonNull
    @PrimaryKey
    private long create_time;

    @ColumnInfo(name = "file_path")
    private String filePath;

    @ColumnInfo(name = "seqno")
    private String seqno;
    /**
     * 代表当前照片的状态 -1未提交 0 提交成功 1提交失败
     */
    @ColumnInfo(name = "status")
    private int status;

    @ColumnInfo(name = "content")
    private String content;

    public BusinessLogBean() {

        create_time = System.currentTimeMillis();

    }

    public String getContent() {
        return content == null ? "" : content;
    }

    public void setContent(String content) {
        this.content = content == null ? "" : content;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

    public String getFilePath() {
        return filePath == null ? "" : filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath == null ? "" : filePath;
    }

    public String getSeqno() {
        return seqno == null ? "" : seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno == null ? "" : seqno;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
