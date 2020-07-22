package com.bondex.ysl.installlibrary.check;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.bondex.ysl.installlibrary.download.HttpConnection;
import com.bondex.ysl.installlibrary.download.UpdateBean;
import com.bondex.ysl.installlibrary.download.UpdateListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * date: 2020/7/22
 *
 * @author: ysl
 * description:
 */
public class CheckUpdate {

    private Context context;
    private DownloadDIalog downloadDIalog;
    private String filePath;

    public CheckUpdate(Context context) {
        this.context = context;
        downloadDIalog = new DownloadDIalog(context);

        filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+ File.separator+"app.apk";
    }

    protected void checkVersion() {

        HttpConnection.checkVersion(new UpdateListener() {
            @Override
            public void update(UpdateBean updateBean) {

                if (updateBean.getVersion_id() > getVersionCode(context)) {
                    //        对比version code 检查是否需要更新
                    if (updateBean.getVersion_remark().contains("type")) {

                        try {
                            JSONObject ob = new JSONObject(updateBean.getVersion_remark());
                            String remark = ob.getString("remark");
                            String type = ob.getString("type");
                            if (type == "photo") {
                                updateBean.setDescription(remark);

                                downloadDIalog.showUpdateDialog(updateBean);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }

            @Override
            public void notUpdate(String msg) {

            }
        });
    }

    public int getVersionCode(Context context) {

        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);

            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return 0;
    }


}
