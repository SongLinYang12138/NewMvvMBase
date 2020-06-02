package com.bondex.ysl.installlibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.FileProvider;

import java.io.File;

/**
 * date: 2018/11/8
 * Author: ysl
 * description:
 */
public class InstallApk {

    private static final String TYPE = "application/vnd.android.package-archive";
    private static final String AUTHORITY = "com.bondex.ysl.photo.fileprovider";

    public static void install(String path, Context context) {

        Intent intent = new Intent(Intent.ACTION_VIEW);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {

            intent.setDataAndType(Uri.fromFile(new File(path)),TYPE);
        }else {
            Uri uri = FileProvider.getUriForFile(context,AUTHORITY,new File(path));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(uri,TYPE);
        }
        context.startActivity(intent);

    }

}
