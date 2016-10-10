package com.grgbanking.baselib.core.callback;

import android.os.Message;

import com.grgbanking.baselib.web.WebCallback;
import com.grgbanking.baselib.web.entity.ErrorMsg;

import okhttp3.Call;
import okhttp3.Response;

public abstract class BaseCallback<T> extends WebCallback {
    public final static String TAG = "BaseCallback";
    public ResultCallback<T> resultCallback;
    public ErrorMsg error;
    public Call call;
    public T resp;
    protected CallbackHandler handler;

    public BaseCallback(ResultCallback<T> resultCallback) {
        this(resultCallback, CallbackHandler.getHandler());
    }

    public BaseCallback(ResultCallback<T> resultCallback, CallbackHandler handler) {
        this.resultCallback = resultCallback;
        this.handler = handler;
    }

    protected void sendMessage(int what) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = this;
        handler.sendMessage(msg);
    }

    @Override
    public void onFailure(Call call, ErrorMsg error) {
        this.error = error;
        sendMessage(CallbackHandler.ERROR);
    }

    @Override
    public void onPre(Call call) {
        this.call = call;
        sendMessage(CallbackHandler.PRE);
    }

    /**
     * 请求服务器并解析数据成功，有特殊需求时可重写该方法
     *
     * @param call
     * @param response
     * @param resp
     */
    public void onSuccess(Call call, Response response, T resp) {
        this.resp = resp;
        sendMessage(CallbackHandler.SUCCESS);
    }
}