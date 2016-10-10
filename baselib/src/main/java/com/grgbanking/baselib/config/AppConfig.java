package com.grgbanking.baselib.config;

import android.content.Context;
import android.os.Environment;

import com.grgbanking.baselib.util.log.LogUtil;

/**
 * 应用配置文件
 */
public class AppConfig {
    public final static String TAG = "AppConfig";
    /**
     * 是否开发测试模式
     */
    public static final boolean DEBUG = true;

    /**
     * 是否允许打印输出日志
     */
    public static final boolean LOG_ENABLE = DEBUG;//测试时，允许打印日志，生产时，默认不输出

    /**
     * 是否输出日志到手机文件
     */
    public static final boolean LOG_SDCARD_ENABLE = DEBUG;//生产时，将日志输出到文件，测试时默认不输出

    /**
     * 存储的根目录
     */
    public static String FILE_ROOT;

    /**
     * 图片缓存根目录
     */
    public static String IMAGE_ROOT;

    /**
     * 文件缓存根目录
     */
    public static String FILE_CACHE;

    /**
     * 日志的目录
     */
    public static String LOG_PATH;

    /**
     * 未捕获异常处理器的日志目录
     */
    public static String CRASH_PATH;

    public static String FILE_FOLDER;//文件夹名称

    public static String APK_NAME;//下载的apk名称

    public static String APK_FILE_PATH;// 下载的apk的路径

    /**
     * @param c
     * @param fileFolder 文件夹名称
     * @param apkName  apk名称
     */
    public static void init(Context c, String fileFolder, String apkName) {
        FILE_FOLDER = fileFolder;
        APK_NAME = apkName;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //如果有外部存储(sd卡)，则使用外部存储路径
            FILE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + FILE_FOLDER + "/";
        } else {
            //没有，则使用应用存储路径
            FILE_ROOT = c.getFilesDir().getAbsolutePath() + "/";
        }

        APK_FILE_PATH = FILE_ROOT + APK_NAME ;

        IMAGE_ROOT = FILE_ROOT + "images/";
        FILE_CACHE = FILE_ROOT + "cache/";
        LOG_PATH = FILE_ROOT + "log/";
        CRASH_PATH = LOG_PATH + "crash/";
        LogUtil.i(TAG, "文件缓存根目录 FILE_ROOT = " + FILE_ROOT);
    }

}
