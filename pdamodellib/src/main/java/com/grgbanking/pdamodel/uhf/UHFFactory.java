package com.grgbanking.pdamodel.uhf;

import android.content.Context;

import com.grgbanking.pdamodel.IModelFactory;
import com.grgbanking.pdamodel.PDAManaufacturers;
import com.grgbanking.pdamodel.uhf.dahua.DaHuaUHFModel;
import com.grgbanking.pdamodel.uhf.idata.IdataUHFModel;

/**
 * 
 * 版权所有：2015-GRGBANKING 项目名称：PDAModel
 * 
 * 类描述：根据不同厂家的标识创建不同厂家的UHF模块 类名称：com.grgbanking.pdamodel.uhf.UHFFactory
 * 创建人：G0212269 创建时间：2015-8-24 下午3:19:11 修改人： 修改时间：2015-8-24 下午3:19:11 修改备注：
 * 
 * @version V1.0
 */
public class UHFFactory implements IModelFactory {

    @Override
    public IUHFModel createModel(Context context) throws IllegalAccessException {
        // TODO Auto-generated method stub
        IUHFModel model = null;
        String manufacturer = android.os.Build.MANUFACTURER;
        String device = android.os.Build.DEVICE;
        if (PDAManaufacturers.DAHUA.equals(device)||PDAManaufacturers.DAHUA1.equals(device)||PDAManaufacturers.DAHUA2.equals(device)) {
            model = DaHuaUHFModel.getInstance();
        } else if (PDAManaufacturers.IDATA_GIONEELY.equals(device)||PDAManaufacturers.IDATA_MSM7627A.equals(device)) {
            model = IdataUHFModel.getInstance();
        } else {
            throw new IllegalAccessException("当前设备不支持该模块");
        }
        return model;
    }

}
