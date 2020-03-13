package com.huaweiHMSdemo.huawei.utils;


import retrofit2.Response;

public interface IHandleAPICallBack<T> {
    void handleWebserviceCallBackSuccess(Response<T> response);
    void handleWebserviceCallBackFailure(String error);
    void onConnectionError();
}
