package com.codingwithmitch.kotlinsingletonexample.repository

import android.app.Application
import com.huaweiHMSdemo.huawei.api.MyRetrofitBuilder
import com.huaweiHMSdemo.huawei.model.ReversGeoCodeMain
import com.huaweiHMSdemo.huawei.utils.IHandleAPICallBack
import com.huaweiHMSdemo.huawei.utils.NetworkUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object Repository {
    var job: CompletableJob? = null
    lateinit var network: NetworkUtils

    fun init(application: Application) {
        network = NetworkUtils(application)
    }

    fun getGeoAdress(body: RequestBody,apikey: String, handler: IHandleAPICallBack<ReversGeoCodeMain>) {


        if (!network.isConnectedToInternet) {
            handler.onConnectionError()
            return
        }

        job = Job()

        job?.let { theJob ->
            CoroutineScope(IO + theJob).launch {
                try {
                    val getGeoCodes = MyRetrofitBuilder.apiService.getReversGeocode(apikey,body)
                    getGeoCodes.enqueue(object :
                        Callback<ReversGeoCodeMain> {
                        override fun onResponse(
                            call: Call<ReversGeoCodeMain>,
                            response: Response<ReversGeoCodeMain>
                        ) {
                            if (response.isSuccessful) {
                                handler.handleWebserviceCallBackSuccess(response)
                            } else {
                                // Handle error returned from server
                                handler.handleWebserviceCallBackFailure(
                                    response.errorBody().toString()
                                )
                            }
                        }

                        override fun onFailure(call: Call<ReversGeoCodeMain>, t: Throwable) {
                            t.printStackTrace()
                            handler.handleWebserviceCallBackFailure(t.message)
                        }
                    })
                } catch (e: Exception) {
                    e.printStackTrace()
                    handler.handleWebserviceCallBackFailure(e.message)
                }
                withContext(Main) {
                    theJob.complete()
                }
            }

        }


    }

    fun cancelJobs() {
        job?.cancel()
    }

}
















