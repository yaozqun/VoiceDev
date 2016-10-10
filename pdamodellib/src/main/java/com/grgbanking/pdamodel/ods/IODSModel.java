package com.grgbanking.pdamodel.ods;

import com.grgbanking.pdamodel.IModel;

import android.content.Context;
import android.os.Handler;

/**
 * 
 * 版权所有：2015-GRGBANKING 项目名称：PDAModel
 * 
 * 类描述：一维护激光扫描模块定义 类名称：com.grgbanking.pdamodel.ods.IODSModel 创建人：G0212269
 * 创建时间：2015-8-24 下午4:36:39 修改人： 修改时间：2015-8-24 下午4:36:39 修改备注：
 * 
 * @version V1.0
 */
public interface IODSModel extends IModel {
    /**
     * 
     * @Title: open
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @param context
     * @param handler
     * @param odsL
     * @param isContinuous
     * @return
     */
    public boolean open(Context context, Handler handler, ODSListener odsL, boolean isContinuous);

    /**
     * 
     * @Title: startOrStop
     * @Description: TODO(当ods在工作时，调用方法停止工作，当ods停止时，调用方法开始工作)
     */
    public void startOrStop();

    /**
     * 
     * @Title: pause
     * @Description: TODO(ods暂停工作)
     */
    public void pause();

    /**
     * 
     * @Title: goOn
     * @Description: TODO(ods继续工作)
     */
    public void goOn();

    /**
     * 
     * 
     * @Title: close
     * @Description: TODO(关闭ods模块)
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
}
