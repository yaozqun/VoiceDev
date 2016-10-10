
package com.grgbanking.baselib.util.log;

import com.grgbanking.baselib.config.AppConfig;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * 日志类包装，方便调用
 * 
 * @author wKF46829
 * @version [v1.0, 2011-4-23]
 * @see
 * @since
 */
public class LogUtil {
  private static final String TAG = "LogUtil";
  // 是否允许输出日志
  private static final boolean LOG_D_ENABLE = AppConfig.LOG_ENABLE;
  private static final boolean LOG_E_ENABLE = true;// 所有异常（Exception）必须输出日志
  private static final boolean LOG_I_ENABLE = AppConfig.LOG_ENABLE;
  private static final boolean LOG_V_ENABLE = AppConfig.LOG_ENABLE;
  private static final boolean LOG_W_ENABLE = AppConfig.LOG_ENABLE;

  // 是否输出日志到手机文件
  private static final boolean LOG_SDCARD_ENABLE = AppConfig.LOG_SDCARD_ENABLE;

  private static final String LOG_D = "D";
  private static final String LOG_E = "E";
  private static final String LOG_I = "I";
  private static final String LOG_V = "V";
  private static final String LOG_W = "W";

  private static final int TIMEMILLIS_SUBSTRING_LENGTH = 10;// 从时间戳中截取的时间长度
  private static final int MIN_LOG_COUNT = 5;// 日志数据
  private static final int SLEEP_TIME = 5000;// 单位：ms

  private static List<String> fileLogs = new ArrayList<String>();
  private static WorkThread workThread;

  private LogUtil() {
  }

  public static boolean isLoggable(String tag, int level) {
    return android.util.Log.isLoggable(tag, level);
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   */
  public static int v(String tag, String msg) {
    if (!LOG_V_ENABLE)
      return 0;
    if (LOG_SDCARD_ENABLE)
      fileLog(tag, LOG_V, msg);
    return android.util.Log.v(tag, msg);
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   * @param tr
   *          An exception to log
   */
  public static int v(String tag, String msg, Throwable tr) {
    return v(tag, msg + '\n' + getStackTraceString(tr));
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   */
  public static int d(String tag, String msg) {
    if (!LOG_D_ENABLE)
      return 0;
    if (LOG_SDCARD_ENABLE)
      fileLog(tag, LOG_D, msg);
    return android.util.Log.d(tag, msg);
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   * @param tr
   *          An exception to log
   */
  public static int d(String tag, String msg, Throwable tr) {
    return d(tag, msg + '\n' + getStackTraceString(tr));
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   */
  public static int i(String tag, String msg) {
    if (!LOG_I_ENABLE)
      return 0;
    if (LOG_SDCARD_ENABLE)
      fileLog(tag, LOG_I, msg);
    return android.util.Log.i(tag, msg);
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   * @param tr
   *          An exception to log
   */
  public static int i(String tag, String msg, Throwable tr) {
    return i(tag, msg + '\n' + getStackTraceString(tr));
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   */
  public static int w(String tag, String msg) {
    if (!LOG_W_ENABLE)
      return 0;
    if (LOG_SDCARD_ENABLE)
      fileLog(tag, LOG_W, msg);
    return android.util.Log.w(tag, msg);
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   * @param tr
   *          An exception to log
   */
  public static int w(String tag, String msg, Throwable tr) {
    return w(tag, msg + '\n' + getStackTraceString(tr));
  }

  /*
   * Send a {@link #WARN} log message and log the exception.
   *
   * @param tag Used to identify the source of a log message. It usually
   * identifies the class or activity where the log call occurs.
   *
   * @param tr An exception to log
   */
  public static int w(String tag, Throwable tr) {
    return w(tag, getStackTraceString(tr));
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   */
  public static int e(String tag, String msg) {
    if (!LOG_E_ENABLE)
      return 0;
    if (LOG_SDCARD_ENABLE)
      fileLog(tag, LOG_E, msg);
    return android.util.Log.e(tag, msg);
  }

  /**
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   * @param tr
   *          An exception to log
   */
  public static int e(String tag, String msg, Throwable tr) {
    return e(tag, msg + '\n' + getStackTraceString(tr));
  }

  /**
   * Handy function to get a loggable stack trace from a Throwable
   *
   * @param tr
   *          An exception to log
   */
  public static String getStackTraceString(Throwable tr) {
    return android.util.Log.getStackTraceString(tr);
  }

  /**
   * Low-level logging call.
   *
   * @param priority
   *          The priority/type of this log message
   * @param tag
   *          Used to identify the source of a log message. It usually
   *          identifies the class or activity where the log call occurs.
   * @param msg
   *          The message you would like logged.
   * @return The number of bytes written.
   */
  public static int println(int priority, String tag, String msg) {
    return android.util.Log.println(priority, tag, msg);
  }

  private static void fileLog(String tag, String level, String msg) {
    String time = new Timestamp(System.currentTimeMillis()).toString();
    fileLogs.add(time + ":  " + level + "/" + tag + ":  " + msg);
    // fileLog(": " + level + "/" + tag + ": " + msg);
    if (workThread == null
        || workThread.getState() == Thread.State.TERMINATED) {
      workThread = new WorkThread();
      workThread.start();
    }
  }

  /**
   * 打印日志到文件
   *
   * @param log
   */
  public static void fileLog(String log) {
    BufferedWriter writer = null;
    try {
      String fileName = new Timestamp(System.currentTimeMillis()).toString()
          .substring(0, TIMEMILLIS_SUBSTRING_LENGTH) + ".txt";
      File logFile = new File(AppConfig.LOG_PATH, fileName);
      if (!logFile.exists()) {
        File dir = logFile.getParentFile();
        if (!dir.exists()) {
          dir.mkdirs();
        }
        logFile.createNewFile();
      }
      writer = new BufferedWriter(new FileWriter(logFile, true));
      writer.write(log);
      writer.newLine();
    } catch (Exception e) {
      android.util.Log.e(TAG, "an error occured when fileLog(String log)", e);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (Exception e) {
        }
      }
    }
  }

  /**
   * 打印日志到文件
   *
   * @param logs
   */
  private static void fileLog(List<String> logs) {
    BufferedWriter writer = null;
    try {
      String fileName = new Timestamp(System.currentTimeMillis()).toString()
          .substring(0, TIMEMILLIS_SUBSTRING_LENGTH) + ".txt";
      File logFile = new File(AppConfig.LOG_PATH, fileName);
      if (!logFile.exists()) {
        File dir = logFile.getParentFile();
        if (!dir.exists()) {
          dir.mkdirs();
        }
        logFile.createNewFile();
      }
      writer = new BufferedWriter(new FileWriter(logFile, true));
      for (String log : logs) {
        writer.write(log);
        writer.newLine();
      }
      writer.close();
      writer = null;
    } catch (Exception e) {
      android.util.Log.e(TAG, "an error occured when fileLog(List<String> logs",
          e);
    } finally {
      if (writer != null) {
        try {
          writer.close();
        } catch (Exception e) {
        }
      }
    }
  }

  /**
   * 内部类，工作线程
   */
  private static class WorkThread extends Thread {
    private static final String TAG = "LogUtil  -  WorkThread";
    // 该工作线程是否有效，用于结束该工作线程
    private boolean isRunning = true;

    @Override
    public void run() {
      while (isRunning) {// 注意，若线程无效则自然结束run方法，该线程就没用了
        // 日志数据小于5条不打印到文件
        if (fileLogs.size() < MIN_LOG_COUNT) {
          try {
            android.util.Log.i(TAG, "Thread.sleep(5000)");
            Thread.sleep(SLEEP_TIME);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          continue;
        }
        List<String> logs = fileLogs;
        fileLogs = new ArrayList<String>();
        android.util.Log.i(TAG, "start fileLog");
        fileLog(logs);
      }
      // 把最后没有写完的日志写完
      List<String> logs = fileLogs;
      fileLogs = new ArrayList<String>();
      fileLog(logs);
      android.util.Log.i(TAG, "WorkThread end");
    }

    // 停止工作，让该线程自然执行完run方法，自然结束
    public void stopWorker() {
      isRunning = false;
    }
  }

  public static void stopFileLog() {
    if (workThread != null) {
      workThread.stopWorker();
    }
  }

}
