package com.grgbanking.baselib.util.log;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.grgbanking.baselib.config.AppConfig;
import com.grgbanking.baselib.util.DateUtil;
import com.grgbanking.baselib.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

/**
 * Logger 打印log，并且写在sd卡
 * 
 * @author wWX173427
 * @version [版本号, 2013-8-6]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class Logger {
    private static final String LOG_TAG = "Logger";
    private static final int SIZE = 1024;

    private Handler handler;

    public Logger() {
        File dir = new File(AppConfig.LOG_PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        HandlerThread handlerThread = new HandlerThread(LOG_TAG);
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        
        File logFile = new File(dir, DateUtil.formatTime(new Date()) + ".txt");
        handler = new LogHandler(looper, logFile);
    }

    /**
     * 写入消息到SD卡
     * @param tag 标记
     * @param level 日志级别
     * @param msg 消息
     */
    private void writeLog(String tag, String level, String msg) {
        Message message = new Message();
        message.obj = DateUtil.formatTime(new Date()) + ":  " + level + "/" + tag + ":  " + msg;
        handler.sendMessage(message);
    }

    public void i(String tag, String msg) {
        if (!AppConfig.LOG_ENABLE) {
            return;
        }
        Log.i(tag, msg);
        writeLog(tag, "I", msg);
    }
    
    public void d(String tag, String msg) {
        if (!AppConfig.LOG_ENABLE) {
            return;
        }
        Log.d(tag, msg);
        writeLog(tag, "D", msg);
    }

    public void w(String tag, String msg) {
        if (!AppConfig.LOG_ENABLE) {
            return;
        }
        Log.w(tag, msg);
        writeLog(tag, "W", msg);
    }

    public void e(String tag, String msg) {
        if (!AppConfig.LOG_ENABLE) {
            return;
        }
        Log.e(tag, msg);
        writeLog(tag, "E", msg);
    }

    static class LogHandler extends Handler {
        private File file;

        public LogHandler(Looper looper, File file) {
            super(looper);
            this.file = file;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != file) {
                String message = msg.obj.toString();
                BufferedWriter writer = null;
                try {
                    writer = new BufferedWriter(new FileWriter(file, true), SIZE);
                    writer.write(message);
                    writer.newLine();
                } catch (IOException e) {
                    Log.e(LOG_TAG, e.toString());
                } finally {
                    FileUtil.closeQuietly(writer);
                }
            }
        }
    }
}
