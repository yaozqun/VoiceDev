package com.grgbanking.baselib.web.response;

/**
 * 返回参数对象
 */
public class VersionInfoResponse {

    public String version;//版本号

    public String url;//下载Url

    public String content;

    /**
     * 是否强制更新
     * 0：不强制更新
     * 1：强制更新
     */
    public String forcedUpdate;

}
