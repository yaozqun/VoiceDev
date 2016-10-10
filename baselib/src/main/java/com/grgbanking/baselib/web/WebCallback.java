package com.grgbanking.baselib.web;

import com.grgbanking.baselib.util.log.LogUtil;
import com.grgbanking.baselib.web.entity.ErrorMsg;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;

public abstract class WebCallback implements Callback {
    public final static String TAG = "WebCallback";

    @Override
    final public void onFailure(Call call, IOException e) {
        if ("Socket closed".equals(e.getMessage())) {
            LogUtil.e(TAG, "onResponse  用户取消操作");
            onFailure(call, new ErrorMsg(ErrorMsg.CODE_CANCEL));
        } else {
            LogUtil.e(TAG, "onFailure  "+ e);
            onFailure(call, new ErrorMsg(ErrorMsg.CODE_WEB_ERROE));
        }


    }

    /*
    * 请求失败
    */
    public abstract void onFailure(Call var1, ErrorMsg error);

    /*
    * 执行请求前
    */
    public abstract void onPre(Call call);
}