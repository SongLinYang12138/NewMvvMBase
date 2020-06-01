package com.bondex.ysl.installlibrary.download;

/**
 * date: 2020/6/1
 *
 * @Author: ysl
 * description:
 */
public class UpdateBean {

    /**
     * version_id : 11
     * version_name : 1.0.11
     * version_remark : 空运更新
     * download_url : http://7xt9ff.com2.z0.glb.qiniucdn.com/83EA6F81-C575-44EE-B50B-2B9AC7B6C236
     * downlad_md5 : c4b2cac8b5d78b396f7ffff34bc8cc67
     */

    private int version_id;
    private String version_name;
    private String version_remark;
    private String download_url;
    private String downlad_md5;
    private String description;

    public String getDescription() {
        return description == null ? "" : description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description;
    }

    public int getVersion_id() {
        return version_id;
    }

    public void setVersion_id(int version_id) {
        this.version_id = version_id;
    }

    public String getVersion_name() {
        return version_name;
    }

    public void setVersion_name(String version_name) {
        this.version_name = version_name;
    }

    public String getVersion_remark() {
        return version_remark;
    }

    public void setVersion_remark(String version_remark) {
        this.version_remark = version_remark;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getDownlad_md5() {
        return downlad_md5;
    }

    public void setDownlad_md5(String downlad_md5) {
        this.downlad_md5 = downlad_md5;
    }

}
