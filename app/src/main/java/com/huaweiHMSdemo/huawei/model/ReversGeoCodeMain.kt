package com.huaweiHMSdemo.huawei.model


import com.google.gson.annotations.SerializedName

data class ReversGeoCodeMain(
    val returnCode: String,
    val returnDesc: String,
    val sites: List<Site>
)