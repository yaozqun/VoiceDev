package com.grgbanking.baselib.web.request;

public class RequestRoot {
    public RequestHeader header;
    public Object request;

    public RequestRoot(RequestHeader header, Object request) {
        this.header = header;
        this.request = request;
    }

    public RequestRoot() {
    }
}
