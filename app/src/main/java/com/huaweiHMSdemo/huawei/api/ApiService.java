package com.huaweiHMSdemo.huawei.api;


import com.huaweiHMSdemo.huawei.model.ReversGeoCodeMain;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @Headers({"Accept:application/json", "Content-Type:application/json"})
    @POST("/mapApi/v1/siteService/reverseGeocode")
    Call<ReversGeoCodeMain> getReversGeocode(@Query("key") String API,@Body RequestBody body);
}
