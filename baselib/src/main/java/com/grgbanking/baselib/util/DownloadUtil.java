package com.grgbanking.baselib.util;

import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.grgbanking.baselib.config.AppConfig;
import com.grgbanking.baselib.util.log.LogUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/9/14.
 */
public class DownloadUtil {
    private static Context context;
    private static DownloadProgressListener downloadProgressListener;
    private static final int PERCENT_TOTAL = 100;

    public static void init(Context cxt) {
        context = cxt;
    }

    public interface DownloadProgressListener {
        void getProgress(int progress);
    }

    /**
     * 调用系统下载器
     *
     * @param url
     * @param title       标题
     * @param description 描述
     */
    public static void downLoadFileByDownloadManager(String url, String title, String description, DownloadProgressListener listener) {
        downloadProgressListener = listener;
        FileUtil.mkdirs(new File(AppConfig.FILE_ROOT));
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(
                context.DOWNLOAD_SERVICE);
        String apkUrl = url;
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(apkUrl));
        File apk = new File(AppConfig.APK_FILE_PATH);
        if (apk.exists()) {
            FileUtil.deleteFile(apk);
        }
        request.setDestinationInExternalPublicDir(AppConfig.FILE_FOLDER, AppConfig.APK_NAME);
        request.setTitle(title);
        request.setDescription(description);
        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        // 设置为可被媒体扫描器找到
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            request.allowScanningByMediaScanner();
        }
        // 设置为可见和可管理
        request.setVisibleInDownloadsUi(true);
        request.setMimeType("application/vnd.android.package-archive");
//        downloadManager.enqueue(request);//下载方法
        context.getContentResolver().registerContentObserver(Uri.parse("content://downloads/"), true, new DownloadObserver(downloadManager, downloadManager.enqueue(request)));
    }

    public static class DownloadObserver extends ContentObserver {
        private long downid;
        private DownloadManager downloadManager;

        public DownloadObserver(DownloadManager downloadManager, long downid) {
            super(new Handler());
            this.downloadManager = downloadManager;
            this.downid = downid;
        }

        @Override
        public void onChange(boolean selfChange) {
            //每当/data/data/com.android.providers.download/database/database.db变化后，触发onCHANGE，开始具体查询
            LogUtil.e("onChangeID", String.valueOf(downid));
            super.onChange(selfChange);
//            //实例化查询类，这里需要一个刚刚的downid
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(downid);
//            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            //这个就是数据库查询啦
            Cursor cursor = downloadManager.query(query);
            while (cursor.moveToNext()) {
                int mDownload_so_far = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                int mDownload_all = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                int mProgress = (mDownload_so_far * PERCENT_TOTAL) / mDownload_all;
                if (downloadProgressListener != null) {
                    downloadProgressListener.getProgress(mProgress);
                }
                LogUtil.e("下载进度", String.valueOf(mProgress));
                //TODO：handler.sendMessage(xxxx),这样就可以更新UI了
            }
        }
    }

}
