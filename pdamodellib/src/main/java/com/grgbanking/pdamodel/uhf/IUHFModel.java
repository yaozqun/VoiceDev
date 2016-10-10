package com.grgbanking.pdamodel.uhf;

import com.grgbanking.pdamodel.IModel;

import android.content.Context;
import android.os.Handler;

/**
 * 版权所有：2015-GRGBANKING 项目名称：PDAModel
 * <p/>
 * 类描述： 类名称：com.grgbanking.pdamodel.IUHFModel 创建人：G0212269 创建时间：2015-8-24
 * 下午3:04:33 修改人： 修改时间：2015-8-24 下午3:04:33 修改备注：
 *
 * @version V1.0
 */
public interface IUHFModel extends IModel {
    /**
     * @param context
     * @param handler
     * @param uhfL
     * @param isContinuous
     * @param isEpcOnly
     * @param powerGrade 功率等级 ，值为1,2,3;代表三个等级，数字越4大，功率越大，扫描距离越远
     * @return
     * @Title: open
     * @Description: TODO(这里用一句话描述这个方法的作用)
     */
    public boolean open(Context context, Handler handler, UHFListener uhfL, boolean isContinuous, boolean isEpcOnly,
                        int powerGrade);

    /**
     * @Title: startOrStop
     * @Description: TODO(当uhf在工作时，调用方法停止工作，当uhf停止时，调用方法开始工作)
     */
    public void startOrStop();

    /**
     * @Title: pause
     * @Description: TODO(uhf暂停工作)
     */
    public void pause();

    /**
     * @Title: goOn
     * @Description: TODO(uhf继续工作)
     */
    public void goOn();

    /**
     * @Title: close
     * @Description: TODO(关闭uhf模块)
     */
    public void close();

    /**
     * @return
     * @Title: isScaning
     * @Description: TODO(获取扫描状态)
     */
    public boolean isScaning();

    /**
     * @param isContinuous
     * @Title: setContinuous
     * @Description: TODO(设置是否连续扫描)
     */
    public void setContinuous(boolean isContinuous);

    /**
     * @param context
     * @param handler
     * @param uhfL
     * @param powerGrade 功率等级 ，值为1,2,3;代表三个等级，数字越4大，功率越大，扫描距离越远
     * @param writeData  写入的数据
     * @return
     * @Title: writeData
     * @Description: 将数据写入标签
     */
    public boolean writeData(Context context, Handler handler, UHFListener uhfL, int powerGrade,String writeData);
}
