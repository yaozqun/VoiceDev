package com.grgbanking.baselib.web.request;


import java.util.UUID;

public class RequestHeader {

    public String loginName;

    public String serialNo = UUID.randomUUID().toString();
    ;

    public Long userId;

    public RequestHeader(String loginName, String serialNo, Long userid) {
        this.loginName = loginName;
        this.serialNo = serialNo;
        this.userId = userid;
    }

    public RequestHeader() {
    }
}
