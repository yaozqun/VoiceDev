package com.grgbanking.pdamodel.uhf;

import java.util.List;

/**
 * 
 * 版权所有：2015-GRGBANKING 项目名称：PDAModel
 * 
 * 类描述：UHF扫描过程及结果监听 类名称：com.grgbanking.pdamodel.uhf.UHFListener 创建人：G0212269
 * 创建时间：2015-8-24 下午2:57:33 修改人： 修改时间：2015-8-24 下午2:57:33 修改备注：
 * 
 * @version V1.0
 */
public interface UHFListener {
    /**
     * UHF扫描结果回调方法
     * 
     * @param epcList
     *            如果是null，表示本次没有扫描到，如果是size>0表明本次扫描到多个标签
     * @param epcHexStrList
     *            将byte[]转成16进制字符串的epcList
     * @param beanList
     *            完整的超高频信息
     */
    public boolean onUHFReader(List<byte[]> epcList, List<String> epcHexStrList, List<UHFCardBean> beanList);

    public void onStartScan();

    public void onStopScan();

    public void onError(String str);

    public void writeData(List<UHFCardBean> beanList,boolean writeSuccess);
}
