package com.grgbanking.pdamodel.fingerprint;

import com.grgbanking.pdamodel.fingerprint.dahua.DahuaFingerPrintModel.FingerData;

import android.graphics.Bitmap;

/**
 * 
 * 版权所有：2015-GRGBANKING 项目名称：PDAModel
 * 
 * 创建时间：2015-8-24 下午4:38:07 修改人： 修改时间：2015-8-24 下午4:38:07 修改备注：
 * 
 * @version V1.0
 */
public interface FingerPrintListener {
    /**
     * 成功扫描到东西后返回数据
     * @Title: onODSReader
     * @Description: TODO(成功扫描到东西后返回数据)
     * @param rawData
     *            未处理过的原始数据
     * @param str
     *            由原始数据转换成的十六进制字符串
     * @return
     */
    public boolean onReader(byte[] rawData, String hexStr, Bitmap bp);

    /**
     * 开始扫描的时候会调用
     * @Title: onStartScan
     * @Description: TODO(扫描结束的时候会调用)
     */
    public void onStartScan();

    /**
     * 
     * 扫描结束的时候会调用
     * @Title: onStopScan
     * @Description: TODO(扫描结束的时候会调用)
     */
    public void onStopScan();

    /**
     * 扫描过程出错了会调用
     * @Title: onError
     * @Description: TODO(扫描过程出错了会调用)
     * @param str 错误信息
     */
    public void onError(String str);

    /**
     *
     * @param score
     * @param fingerData
     * @param fingerByte
     */
    public void onCompareSuccess(int score, FingerData fingerData, byte[] fingerByte);
}
