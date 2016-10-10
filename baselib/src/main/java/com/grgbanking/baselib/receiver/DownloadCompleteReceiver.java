package com.grgbanking.baselib.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.grgbanking.baselib.config.AppConfig;
import com.grgbanking.baselib.util.UpgradeUtil;
import com.grgbanking.baselib.util.log.LogUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/9/14.
 */
public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e("安装apk", "安装apk");
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            UpgradeUtil.installApk(context, new File(AppConfig.APK_FILE_PATH));
        }
    }
}
