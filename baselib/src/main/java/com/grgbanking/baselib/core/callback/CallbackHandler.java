package com.grgbanking.baselib.core.callback;

import android.os.Handler;
import android.os.Message;

public class CallbackHandler extends Handler {
    public final static int ERROR = -1;
    public final static int SUCCESS = 1;
    public final static int PRE = 2;
    private static CallbackHandler handler;

    static {
        handler = new CallbackHandler();
    }

    public static CallbackHandler getHandler() {
        return handler;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        BaseCallback cb = (BaseCallback) msg.obj;
        if (cb == null || cb.resultCallback == null) {
            return;
        }
        switch (msg.what) {
            case ERROR:
                cb.resultCallback.onError(cb.error);
                break;
            case SUCCESS:
                cb.resultCallback.onSuccess(cb.resp);
                break;
            case PRE:
                cb.resultCallback.onPre(cb.call);
                break;
            default:
                break;
        }
    }
}
