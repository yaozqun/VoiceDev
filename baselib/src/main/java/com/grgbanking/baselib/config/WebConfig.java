package com.grgbanking.baselib.config;

/**
 * Created by Administrator on 2016/9/18.
 */
public class WebConfig {
    //版本更新接口请求地址
//    public static final String HOST = "";//生产外网
//    public static final String HOST = "http://10.2.15.250:8081/mm";//测试---李倩
    public static final String HOST = "http://10.2.8.68:8182";//测试环境


    /**
     * 获取最新版本信息
     */
    public static final String VERSION_UPGRADE = HOST + "/pc/versionInfo/queryNewVersion.do";

    /**
     * 上传日志  --  外网
     */
    public static final String URL_UPLOAD_LOG = "http://183.63.190.42:19180/action/uploadRecord/uploadErrorFile.do";
}
