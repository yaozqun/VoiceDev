package com.BRMicro;

import java.io.DataOutputStream;

import android.util.Log;

/*
 * 达华_指纹模块_JNI类
 */
public class NETLH_E {

    private final String TAG = "=NETLH_E=";

    public NETLH_E() {
        String sCurrentPath = "/mnt/sdcard/";
        char[] pCurrentPath = sCurrentPath.toCharArray();
        // SetAppDirectoryPath(pCurrentPath, 14);

        char[] ppCurrentPath = new char[1024];
        GetAppDirectoryPath(ppCurrentPath, 1024);
        String ssCurrentPath = ppCurrentPath.toString();
    }

    public native int CmdDeviceInitGetPath(byte[] path);

    public int CmdDeviceGetChmod(int ErrCode) {
        int ret = 1;
        byte[] path = new byte[128];

        CmdDeviceInitGetPath(path);
        String spath = new String(path);
        String sspath = spath.substring(0, spath.indexOf('\0'));
        Process process = null;
        DataOutputStream os = null;
        String command = "chmod 777 " + sspath;
        Log.d(TAG, " exec " + command);
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            ret = 0;
        }

        return ret;
    }

    public int CmdDeviceGetChmod(String path) {
        String command = "chmod 777 " + path;
        Log.d(TAG, " exec " + command);
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
        }
        return 0;
    }

    public native int GetComList(char[] ComList); // /

    public native int AsciiToHex(char[] _pInData, int _nInLength, char[] _pOutData, int[] _nOutLength);// /

    public native int GetCurrentDirectoryPath(char[] _pCurrentPath, int _pLenth);// /

    public native int GetAppDirectoryPath(char[] _pCurrentPath, int _pLenth); // /

    public native int SetAppDirectoryPath(char[] _pCurrentPath, int _pLenth); // /

    public native int ConfigCommParameterUDisk(int _DeviceAdd, // 设备地址
            int _Password); // 联机密码

    public native int ConfigCommParameterCom(String _COM, // 串口号
            int _BaudRate, // 波特率
            int _DataBit, // 数据位
            int _StopBit, // 停止位
            int _CheckMode, // 校验方式
            int _DeviceAdd, // 设备地址
            int _Password); // 联机密码

    public native int CmdDeviceReset(int _ErrFlag[]); // 复位模块

    public native int CmdDetectFinger(int _ErrFlag[]);// 探测手指

    public native int CmdGetRawImage(int _ErrFlag[]);// 获取原始指纹图像

    public native int CmdGetRedressImage(int _DetectDn, int _ErrFlag[]);// /获取矫正指纹图像

    public native int CmdUpLoadRawImage(byte[] _ImageBuf);// 上传原始指纹图像

    public native int CmdUpLoadRedressImage(byte[] _ImageBuf);// 上传指纹图像

    public native int CmdDownLoadImage(byte[] _ImagePath);//

    public native int CmdGenChar(int iBuffer, int _ErrFlag[]);// 生成指纹特征

    public native int CmdMatchChar(int _RetScore[], int _ErrFlag[]);// 对比模板

    public native int CmdStoreChar(int m_Addr, int _RetMbIndex[], int _RetScore[], int _ErrFlag[]); // 存储指纹模板

    public native int CmdSearchChar(int iBuffer, int _RetMbIndex[], int _RetScore[], int _ErrFlag[]); // 搜索指纹模板

    public native int CmdGetChar_eAlg(int iBuffer, byte[] CharBuf, int[] _ErrFlag);// /上传模板

    public native int CmdGetChar_xAlg(int iBuffer, byte[] CharBuf, int[] _ErrFlag);// /

    public native int CmdPutChar_eAlg(int iBuffer, byte[] CharBuf, int[] _ErrFlag);// /下载模板

    public native int CmdPutChar_xAlg(int iBuffer, byte[] CharBuf, int[] _ErrFlag);// /

    public native int CmdGetMBIndex(byte[] gMBIndex, int gMBIndexStart, int gMBIndexNum, int _ErrFlag[]); // 获取指纹模板索引

    public native int CmdEmptyChar(int _ErrFlag[]);// 删除所有指纹模板

    public native int CmdDelChar(int m_Addr, int _ErrFlag[]);// 删除指定地址的指纹模板

    public native int CmdVerifyChar(int iBuffer, int m_Addr, int _RetScor[], int _ErrFlag[]); // 单一比对指纹模板

    public native int CmdReadNoteBook(int _PageID, byte[] _NoteText, int _ErrFlag[]); // 读记事本

    public native int CmdWriteNoteBook(int _PageID, byte[] _NoteText, int _ErrFlag[]); // 写记事本

    public native int CmdReadParaTable(PARA_TABLE _ParaTable, int _ErrFlag[]); // 读取参数列表

    /*
     * PARA_TABLE ParaTable = new PARA_TABLE(); CmdReadParaTable(ParaTable, );
     */
    public native int CmdSetBaudRate(int _BaudRate, int _ErrFlag[]); // 设置波特率

    public native int CmdSetSecurLevel(int _SecurLevel, int _ErrFlag[]); // 设置指纹安全级别

    public native int CmdSetCmosPara(int _ExposeTimer, int DetectSensitive, int _ErrFlag[]); //

    public native int CmdGetRawImageBuf(byte[] _ImageBuf); // 获取原始指纹图像

    public native int CmdEraseProgram(int _ErrFlag[]); //

    public native int CmdResumeFactory(int _ErrFlag[]);// 恢复出厂设置

    public native void CommClose(); // 鍏抽棴鎵�湁閫氫俊鏂瑰紡

    public native int GetLastCommErr();

    public native int GetLastCommSystemErr();

    public native void SetTimeOutValue(int _TimeOutValue);

    public native int GetTimeOutValue();

    public native int CmdUpLoadRedressImage256x360(byte[] _ImageBuf);

    public native int CmdMergeChar(int[] _RetScore, int[] _ErrFlag); // 合并特征值

    public native int CmdStoreCharDirect_eAlg(int m_Addr, byte[] _FingerChar, int[] _ErrFlag);// 直接存储指纹模板

    public native int CmdStoreCharDirect_xAlg(int m_Addr, byte[] _FingerChar, int[] _ErrFlag);

    public native int CmdReadCharDirect_eAlg(int m_Addr, byte[] _FingerChar, int[] _ErrFlag); // 直接读取指纹模板

    public native int CmdReadCharDirect_xAlg(int m_Addr, byte[] _FingerChar, int[] _ErrFlag);

    public native int CmdSetPsw(int _NewPsw, int[] _ErrFlag);

    public native int CmdSetDeviceAddress(int _NewAddress, int[] _ErrFlag);

    public native int CmdGetRandom(int[] _Random, int[] _ErrFlag); // 获取随机数

    public native int CmdSendDemon(byte[] data, int[] ErrFlag);

    static {
        System.loadLibrary("NETLH_E");
    }

}
