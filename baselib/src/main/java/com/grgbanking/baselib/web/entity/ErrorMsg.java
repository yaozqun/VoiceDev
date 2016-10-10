package com.grgbanking.baselib.web.entity;

public class ErrorMsg extends Exception {
    public static final String MSG_DEFAULT_ERROR = "对不起，我出错了~";
    public static final String MSG_NO_NETWORK = "亲，没网络连接啦~";
    public static final String MSG_WEB_ERROE = "亲，网络请求出错啦~";
    public static final String MSG_DOWNLOAD_ERROE = "亲，下载出错，请检查下载地址~";
    public static final String MSG_CANCEL = "用户已取消~";


    public static final int CODE_NO_NETWORK = -101;
    public static final int CODE_WEB_ERROE = -102;
    public static final int CODE_DOWNLOAD_ERROE = -103;
    public static final int CODE_CANCEL = -104;

    public int code;
    public String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ErrorMsg(int code) {
        this.code = code;
        message = getErrorMsg(code);
    }

    public ErrorMsg(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static String getErrorMsg(int code) {
        switch (code) {
            case CODE_NO_NETWORK:
                return MSG_NO_NETWORK;
            case CODE_WEB_ERROE:
                return MSG_WEB_ERROE;
            case CODE_DOWNLOAD_ERROE:
                return MSG_DOWNLOAD_ERROE;
            case CODE_CANCEL:
                return MSG_CANCEL;
            default:
                return MSG_DEFAULT_ERROR;
        }
    }
}
