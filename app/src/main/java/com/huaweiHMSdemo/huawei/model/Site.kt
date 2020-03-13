package com.huaweiHMSdemo.huawei.model


import com.google.gson.annotations.SerializedName

data class Site(
    val address: Address,
    val formatAddress: String,
    val location: Location,
    val name: String,
    val siteId: String,
    val viewport: Viewport
)