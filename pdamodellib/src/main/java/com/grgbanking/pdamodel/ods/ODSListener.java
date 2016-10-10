package com.grgbanking.pdamodel.ods;

/**
 * 
 * 版权所有：2015-GRGBANKING 项目名称：PDAModel
 * 
 * 类描述：一维扫描模块过程及结果监听 类名称：com.grgbanking.pdamodel.ods.ODSListener 创建人：G0212269
 * 创建时间：2015-8-24 下午4:38:07 修改人： 修改时间：2015-8-24 下午4:38:07 修改备注：
 * 
 * @version V1.0
 */
public interface ODSListener {
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
    public boolean onODSReader(byte[] rawData, String hexStr);

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
}
