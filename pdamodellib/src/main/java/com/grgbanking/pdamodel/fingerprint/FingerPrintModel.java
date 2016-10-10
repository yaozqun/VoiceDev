package com.grgbanking.pdamodel.fingerprint;

import java.util.List;

import com.grgbanking.pdamodel.IModel;
import com.grgbanking.pdamodel.fingerprint.dahua.DahuaFingerPrintModel.FingerData;

/**
 * 
 * 版权所有：2015-GRGBANKING 项目名称：PDAModel
 * 
 * 类描述： 类名称：com.grgbanking.pdamodel.IUHFModel 创建人：G0212269 创建时间：2015-8-24
 * 下午3:04:33 修改人： 修改时间：2015-8-24 下午3:04:33 修改备注：
 * 
 * @version V1.0
 */
public interface FingerPrintModel extends IModel {
    /**
     * 
     * @Title: open
     * @return
     */
    public boolean open(FingerPrintListener listener);

    /**
     * 
     * @Title: startOrStop
     * @Description: TODO(当uhf在工作时，调用方法停止工作，当uhf停止时，调用方法开始工作)
     */
    public void startOrStop();

    /**
     * 
     * @Title: pause
     * @Description: TODO(uhf暂停工作)
     */
    public void pause();

    /**
     * 
     * @Title: goOn
     * @Description: TODO(uhf继续工作)
     */
    public void goOn();

    /**
     * 
     * 
     * @Title: close
     * @Description: TODO(关闭uhf模块)
     */
    public void close();

    /**
     * @Title: isScaning
     * @Description: TODO(获取扫描状态)
     * @return 
     */
    public boolean isScaning();

    /**
     * @Title: setContinuous
     * @Description: TODO(设置是否连续扫描)
     * @param isContinuous 
     */
    public void setContinuous(boolean isContinuous);

    void setHasImage(boolean ishas);

    void compareFinger(List<FingerData> fingerDatas);

}
