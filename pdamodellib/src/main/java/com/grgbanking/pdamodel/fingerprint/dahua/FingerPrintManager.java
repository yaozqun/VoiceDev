package com.grgbanking.pdamodel.fingerprint.dahua;

import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.BRMicro.SerialPort;
import com.BRMicro.NETLH_E;
import com.BRMicro.PARA_TABLE;

public class FingerPrintManager {

    public static String TAG = "FingerPrintManager";

    private static FingerPrintManager manager;

    private NETLH_E netlh; // 指纹操作句柄

    private SerialPort mSerialPort;// 串口操作句柄

    /* 串口参数 */
    private String comPath = "/dev/ttyMT1";// 串口地址

    private int baudRate = 115200; // 波特率

    private int dataBit = 8;// 数据位

    private int stopBit = 2;// 停止位

    private int checkMode = 0;// 校验位

    private int deviceAdd = 0xffffffff;// 设备地址

    private int password = 0xffffffff;// 密码

    private int ret = 0; // 操作返回标识

    private final int SUCCESS = 1; // 操作成功

    private final int FAIL = 0; // 操作失败

    public static final int BUFFER_A = 0; // 缓冲区A

    public static final int BUFFER_B = 1; // 缓冲区B

    private final String mFingerImagePath = "/mnt/sdcard/Finger.bmp";// 临时保存指纹图片的路径，此路径不能修改

    private boolean isOpen;

    private FingerPrintManager() {
        netlh = new NETLH_E();
        mSerialPort = new SerialPort();
    };

    public static FingerPrintManager getInstance() {
        if (manager == null) {
            synchronized (FingerPrintManager.class) {
                if (manager == null) {
                    manager = new FingerPrintManager();
                }
            }
        }
        return manager;
    }

    /**
     * 打开设备
     * @return
     */
    public boolean open() {
        // String openDevice =
        // sendMSG("open device");
        Log.d(TAG, "打开设备");
        // 打开设备供电
        // mSerialPort.rfidPoweron();
        mSerialPort.psam_poweron();
        // mSerialPort.zigbee_poweron();
        // mSerialPort.power3v3on();
        // mSerialPort.setGpioHigh(74);
        // 切换到串口12
        mSerialPort.switch2Channel(12);
        sleep(1000);
        ret = netlh.ConfigCommParameterCom(comPath, // 串口地址
                baudRate, // 波特率
                dataBit, // 数据位
                stopBit, // 停止位
                checkMode,// 校验位
                deviceAdd,// 设备地址
                password);// 密码
        if (ret == FAIL) {// 打开失败，再次打开
            sleep(1000);
            netlh.CmdDeviceGetChmod(comPath);
            ret = netlh.ConfigCommParameterCom(comPath, // 串口地址
                    baudRate, // 波特率
                    dataBit, // 数据位
                    stopBit, // 停止位
                    checkMode,// 校验位
                    deviceAdd,// 设备地址
                    password);// 密码
        }
        if (ret == FAIL) {
            Log.d(TAG, "打开设备失败");
            return false;
        }
        Log.d(TAG, "打开设备成功");
        isOpen = true;
        return true;
    }

    public boolean isOpen() {
        return isOpen;
    }

    /**
     * 关闭设备
     */
    public void close() {
        Log.d(TAG, "close device");
        if (mSerialPort != null) {
            mSerialPort.psam_poweroff();
            // mSerialPort.rfidPoweroff();
            // mSerialPort.power3v3off();
            // mSerialPort.setGpioLow(74);
        }
        if (netlh != null) {
            netlh.CommClose();
        }
        isOpen = false;
    }

    int[] _ErrFlag = new int[512]; // 错误码存储区

    /**
     * 获取指纹图像,指纹图像缓存的SD目录下 ，需程序加SD卡写卡权限
     * @return 
     */
    public Bitmap getFPImage() {
        Log.d(TAG, "获取指纹图像...");
        Bitmap bp = null;
        // 探测是否有手指
        ret = netlh.CmdDetectFinger(_ErrFlag);
        if (ret == 1 && _ErrFlag[0] == 0) {// 手指在传感器上
            netlh.CmdGetRedressImage(0, _ErrFlag);// 获取指纹图像
            if (_ErrFlag[0] == 0) {// 获取到指纹图像
                ret = netlh.CmdUpLoadRedressImage(null); // 上传图象
                if (ret == 1) {
                    bp = BitmapFactory.decodeFile(mFingerImagePath, null);
                    Log.d(TAG, "获取指纹图像成功");
                    return bp;
                }
            }
        }
        Log.d(TAG, "错误码：" + _ErrFlag[0] + ":" + getErrorMsg(_ErrFlag[0]));
        return null;
    }

    public Object[] getFPImageAndChara(int buffer) {
        Log.d(TAG, "获取指纹...");
        Bitmap bp = null;
        byte[] templet = null;
        // 探测是否有手指
        ret = netlh.CmdDetectFinger(_ErrFlag);
        if (ret == 1 && _ErrFlag[0] == 0) {// 手指在传感器上
            netlh.CmdGetRedressImage(0, _ErrFlag);// 获取指纹图像
            if (_ErrFlag[0] == 0) {// 获取到指纹图像
                ret = netlh.CmdUpLoadRedressImage(null); // 上传图象
                if (ret == 1) {
                    bp = BitmapFactory.decodeFile(mFingerImagePath, null);
                    Log.d(TAG, "获取指纹图像成功");
                }
                ret = netlh.CmdGenChar(buffer, _ErrFlag);
                if (ret == 1 && _ErrFlag[0] == 0) {
                    templet = getChara(buffer);
                }
                if (templet != null && bp != null) {
                    return new Object[] { templet, bp };
                }

            }
        }
        Log.d(TAG, "错误码：" + _ErrFlag[0] + ":" + getErrorMsg(_ErrFlag[0]));
        return null;
    }

    /**
     * 生成指纹特征，将特征存储于缓存区buffer中，buffer为指纹模块中的BUFFER_A或BUFFER_B
     * @param buffer 指纹模块存储区
     * @return
     */
    public boolean genChara(int buffer) {
        Log.d(TAG, "生成指纹特征...");
        // 探测是否有手指
        ret = netlh.CmdDetectFinger(_ErrFlag);
        if (ret == 1 && _ErrFlag[0] == 0) {// 手指在传感器上
            netlh.CmdGetRedressImage(0, _ErrFlag);// 获取指纹图像
            if (_ErrFlag[0] == 0) {// 获取到指纹图像
                // 生成特征模板
                ret = netlh.CmdGenChar(buffer, _ErrFlag);
                if (ret == 1 && _ErrFlag[0] == 0) {
                    if (buffer == BUFFER_A) {
                        Log.d(TAG, "生成指纹特征A");
                    } else {
                        Log.d(TAG, "生成指纹特征B");
                    }
                    return true;
                }
            }
        }
        Log.d(TAG, "错误码：" + _ErrFlag[0] + ":" + getErrorMsg(_ErrFlag[0]));
        return false;
    }

    /**
     * 上传指纹模板，从指纹模块的存储区获取指纹模板数据，大小为256字节
     * @param bufferID
     * @return
     */
    public byte[] getChara(int bufferID) {
        Log.d(TAG, "上传指纹模板...");
        byte[] templet = new byte[256];
        // 以eAlg算法获取模板
        ret = netlh.CmdGetChar_eAlg(bufferID, templet, _ErrFlag);
        if (ret == 1 && _ErrFlag[0] == 0) {
            Log.d(TAG, "上传指纹模板成功");
        } else {
            templet = null;
            Log.d(TAG, "错误码：" + _ErrFlag[0] + ":" + getErrorMsg(_ErrFlag[0]));
        }
        return templet;
    }

    /**
     * 下载指纹模板，从PC上下载一个指纹模板到的一个缓冲区中,buffer_a,buffer_b
     * @param bufferID 缓冲区
     * @param templet  模板数据
     * @return
     */
    public boolean putChara(int bufferID, byte[] templet) {
        ret = netlh.CmdPutChar_eAlg(bufferID, templet, _ErrFlag);
        if (ret == 1 && _ErrFlag[0] == 0) {
            return true;
        }
        return false;
    }

    PARA_TABLE ptab = new PARA_TABLE();// 系统参数表

    /**
     * 存储指纹模板
     */
    public boolean mergeChara(int addr) {
        Log.d(TAG, "存储指纹模板...");
        int[] _RetScore = new int[10];// 分值
        int[] _RetMbIndex = new int[10];
        // 合并特征
        ret = netlh.CmdMatchChar(_RetScore, _ErrFlag);
        if (ret == 1 && _ErrFlag[0] == 0) {
            /**
             * 参数说明：
             * 10  写入的模板地址
             * _RetMbIndex 如果当模块指纹库中有指纹模板，则返回该地址；
             * _RetScore 对比分值
             */
            ret = netlh.CmdStoreChar(addr, _RetMbIndex, _RetScore, _ErrFlag);
            if (ret == 1 && _ErrFlag[0] == 0) {
                // 存入模板成功
                Log.d(TAG, "存储指纹模板成功，地址为：" + addr);
                return true;
            }
        }
        Log.d(TAG, "错误码：" + _ErrFlag[0] + ":" + getErrorMsg(_ErrFlag[0]));
        return false;
    }

    /**
     * 对比bufferA,buferB中的特征，返回对比分值
     * @return
     */
    public int matchChara() {
        Log.d(TAG, "比对BUFFER_A和BUFFER_B中的数据...");
        int[] _RetScore = new int[10];// 分值
        Arrays.fill(_RetScore, 0);
        ret = netlh.CmdMatchChar(_RetScore, _ErrFlag);
        if (ret == 1 && _ErrFlag[0] == 0) {
            // 存入模板成功
            Log.d(TAG, "比对成功，分值为：" + _RetScore[0]);
            return _RetScore[0];
        }
        return 0;
    }

    /**
     * 指纹模板单一比对，当分值大于50时，对比通过
     * @param bufferID  缓冲区ID
     * @param iAddr  指纹模块模板存储地址
     * @return int 对比分值
     */
    public int verifyChara(int bufferID, int iAddr) {
        int[] _RetScore = new int[10];
        ret = netlh.CmdVerifyChar(bufferID, iAddr, _RetScore, _ErrFlag);
        return 0;
    }

    /**
     * 登记模板,
     * @param addr 指纹模板地址
     */
    public boolean enroll(int addr) {
        // 生成指纹特征A //生成指纹特征B
        if (genChara(BUFFER_A) && genChara(BUFFER_B)) {
            // 存储指纹
            return mergeChara(addr);
        }

        return false;
    }

    /**
     * 获取指纹模板索引，返回boolean数组，若当前索引的值为true则有模板数据，若为false则没有模板数据
     * @return
     */
    public boolean[] getFreeAddress() {
        Log.d(TAG, "获取指纹模板索引...");
        boolean[] fingerFlag = new boolean[1000];
        Arrays.fill(fingerFlag, false);
        byte[] gMBIndex = new byte[17 + 820 + 2 + 10];
        for (int i = 0; i < fingerFlag.length; i += 250) {
            Arrays.fill(gMBIndex, (byte) 0x00);
            /**
             * //读取指纹模板索引
             * gMBIndex 存放获取到的指纹模板索引 (1:有模板， 0:没有模板)
             * i 起始地址
             * 250 读取长度
             */
            ret = netlh.CmdGetMBIndex(gMBIndex, i, 250, _ErrFlag);
            if (ret == 1 && _ErrFlag[0] == 0) {
                for (int y = 0; y < 250; y++) {
                    // 有指纹模板存在
                    if (gMBIndex[y] != 0x00) {
                        int index = i + y;
                        fingerFlag[index] = true;
                        Log.d(TAG, "地址[" + index + "] 已存储指纹模板");
                    }

                }
            }
        }
        return fingerFlag;
    }

    // 搜索指纹,搜索已经存在FLASH中的指纹模板。
    public int searchChara(int bufferID) {
        Log.d(TAG, "搜索指纹...");
        int[] _RetMbIndex = new int[10];
        int[] _RetScore = new int[10];
        // 获取指纹特征
        if (genChara(bufferID)) {
            ret = netlh.CmdSearchChar(bufferID, _RetMbIndex, _RetScore, _ErrFlag);
            if (ret == 1 && _ErrFlag[0] == 0) {
                Log.d(TAG, "搜索到指纹模板，地址为[" + _RetMbIndex[0] + "]");
                return _RetMbIndex[0];
            } else {
                Log.d(TAG, "错误码为[" + _ErrFlag[0] + "]:" + getErrorMsg(_ErrFlag[0]));
            }
        }
        return -1;
    }

    // 删除所有指纹模板
    public boolean emptyChara() {
        Log.d(TAG, "删除所有指纹模板...");
        ret = netlh.CmdEmptyChar(_ErrFlag);
        if (ret == 1 && _ErrFlag[0] == 0) {
            Log.d(TAG, "删除所有指纹模板数据成功");
            return true;
        }
        return false;
    }

    private void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static final String[] ERRORMSG = { "命令执行完毕或 OK", "数据包接收错误", "设备地址错误", "通信密码错误", "传感器上没有手指", "从传感器上获取图像失败",
            "生成特征失败", "指纹不匹配", "没搜索到指纹", "特征合并失败", "将模板存库时地址序号超出指纹库范围", "从指纹库读模板出错", "上传特征失败", "上传图像失败", "删除模板失败",
            "清空指纹库失败", "残留指纹或传感器窗口的按指长时间未移开", "指定位置没有有效模板", "指纹重复，需要注册的指纹已经在 FLASH 中注册", "该地址中不存在指纹模板", "获取模板索引长度溢出",
            "设置波特率失败", " 擦除程序标志失败", "系统复位失败", "操作 FLASH 出错", "记事本地址溢出", "设置参数错误", "命令不存在" };

    public static String getErrorMsg(int code) {
        if (-1 < code && code <= ERRORMSG.length) {
            return ERRORMSG[code];
        }
        return "";
    }

}
