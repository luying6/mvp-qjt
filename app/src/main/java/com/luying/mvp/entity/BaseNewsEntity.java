package com.luying.mvp.entity;

/**
 * 创建人:luying
 * 创建时间:2018/5/11.
 */

public class BaseNewsEntity<T> {
//    {
//        "succeed": true,
//            "code": null,
//            "msg": null,
//            "requestId": d9c3583a79a4442ea0745ae905f97f06,
//    }

    private boolean succeed;
    private String code;
    private String msg;
    private String requestId;
    private T data;
    public boolean isSucceed() {
        return succeed;
    }

    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
