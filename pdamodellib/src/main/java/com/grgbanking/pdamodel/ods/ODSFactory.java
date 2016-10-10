package com.grgbanking.pdamodel.ods;

import android.content.Context;
import com.grgbanking.pdamodel.IModelFactory;
import com.grgbanking.pdamodel.PDAManaufacturers;
import com.grgbanking.pdamodel.ods.dahua.DaHuaODSModel;

public class ODSFactory implements IModelFactory {

    @Override
    public IODSModel createModel(Context context) throws IllegalAccessException {
        // TODO Auto-generated method stub
        IODSModel model = null;
        String manufacturer = android.os.Build.MANUFACTURER;
        String device = android.os.Build.DEVICE;
        if (PDAManaufacturers.DAHUA.equals(device)||PDAManaufacturers.DAHUA1.equals(device)||PDAManaufacturers.DAHUA2.equals(device)) {
            model = DaHuaODSModel.getInstance();
        } else {
            throw new IllegalAccessException("当前设备不支持该模块");
        }
        return model;
    }

}
