package com.seatwe.zsws.constant;

/**
 * Created by charry on 2016/10/6.
 */
public class UrlConstant {
    // 系统
    public static String HOST = "http://st.saeese.net/zsws/";
    //基础接口
    public static String BASE_HOST = "http://st.saeese.net/baselib/";

    // 登录
    public static String USER_LOGIN = HOST + "userLogin.do";

    // 到达节点
    public static String ARRIVE_NODE = HOST + "arriveNode.do";

    // 任务信息下载
    public static String TASK_INFO = HOST + "taskInfo.do";

    // 任务记录上传
    public static String UPLOAD_RECORD = HOST + "uploadRecord.do";

    // 退出
    public static String USER_LOGOUT = HOST + "userLogout.do";

    //修改密码
    public static String CHANGE_PASSWORD = BASE_HOST + "changePassword.do";

    //获取钞箱信息
    public static String GET_CASHBOX = BASE_HOST + "getCashBox.do";

    // 获取网点信息
    public static String GET_NET_INFO = HOST + "getNetInfo.do";

    //获取apk升级信息
    public static String GET_APK_UPDATE = BASE_HOST + "getApkUpdate.do";
}
