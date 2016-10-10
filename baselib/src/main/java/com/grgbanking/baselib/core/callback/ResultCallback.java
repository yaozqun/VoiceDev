package com.grgbanking.baselib.core.callback;

import com.grgbanking.baselib.web.entity.ErrorMsg;

import okhttp3.Call;

public interface ResultCallback<T> {
    /*
    * 执行成功
    */
    void onSuccess(T resp);
    /*
    * 执行失败，回调
    */
    void onError(ErrorMsg errorMsg);
    /*
    * 执行请求前,回调
    */
    void onPre(Call call);
}
