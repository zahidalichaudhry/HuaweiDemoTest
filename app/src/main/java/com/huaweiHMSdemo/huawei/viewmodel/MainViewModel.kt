package com.huaweiHMSdemo.huawei.viewmodel

import android.app.Activity
import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import androidx.collection.ArrayMap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.codingwithmitch.kotlinsingletonexample.repository.Repository
import com.huawei.hms.location.FusedLocationProviderClient
import com.huawei.hms.location.LocationServices
import com.huawei.hms.maps.model.LatLng
import com.huaweiHMSdemo.huawei.api.ApiUtils
import com.huaweiHMSdemo.huawei.model.ReversGeoCodeMain
import com.huaweiHMSdemo.huawei.utils.IHandleAPICallBack
import com.huaweiHMSdemo.huawei.utils.ResultWrapper
import com.huaweiHMSdemo.huawei.utils.Status
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Response


class MainViewModel(application: Application) : AndroidViewModel(application) {

    //    var context: Context = application
    var _application: Application = application

    var currentLocation: MutableLiveData<ResultWrapper<Location>> = MutableLiveData()

    var repository: Repository = Repository

    var geoCodedAdressCurrentLocation: MutableLiveData<ResultWrapper<ReversGeoCodeMain>> =
        MutableLiveData()
    var geoMarkerSelectedAdress: MutableLiveData<ResultWrapper<ReversGeoCodeMain>> =
        MutableLiveData()

    val apiKey: String = ApiUtils.APIKEY

    private lateinit var fusedLocationClient: FusedLocationProviderClient


    fun getCurrentAddressFromLatLong(latLng: LatLng) {

        repository.init(_application)



        geoCodedAdressCurrentLocation.postValue(
            ResultWrapper<ReversGeoCodeMain>(
                Status.status.LOADING,
                null, "Loading"
            )
        )
        val body = getJsonEncode(latLng)
        if (body != null) {
            Repository.getGeoAdress(body, apiKey, object :
                IHandleAPICallBack<ReversGeoCodeMain> {
                override fun handleWebserviceCallBackSuccess(response: Response<ReversGeoCodeMain>) {
                    geoCodedAdressCurrentLocation.postValue(
                        ResultWrapper<ReversGeoCodeMain>(
                            Status.status.SUCCESS,
                            response.body(),
                            "Success"
                        )
                    )
                }

                override fun handleWebserviceCallBackFailure(error: String?) {
                    geoCodedAdressCurrentLocation.postValue(
                        ResultWrapper<ReversGeoCodeMain>(
                            Status.status.ERROR,
                            null,
                            error
                        )
                    )
                }


                override fun onConnectionError() {
                    geoCodedAdressCurrentLocation.postValue(
                        ResultWrapper<ReversGeoCodeMain>(
                            Status.status.ERROR,
                            null,
                            "Connection Error"
                        )
                    )
                }

                override fun handleWebserviceCallBackFailure(error: Int?) {
                    geoCodedAdressCurrentLocation.postValue(
                        ResultWrapper<ReversGeoCodeMain>(
                            Status.status.NOREUSLT,
                            null,
                            "NO Address Found"
                        )
                    )
                }


            })
        }
    }

    fun getMarkerAddressFromLatLong(latLng: LatLng) {

        repository.init(_application)

//        val encodeAPI = URLEncoder.encode(apiKey, "utf-8")

        geoMarkerSelectedAdress.postValue(
            ResultWrapper<ReversGeoCodeMain>(
                Status.status.LOADING,
                null, "Loading"
            )
        )
        val body = getJsonEncode(latLng)
        if (body != null) {
            Repository.getGeoAdress(body, apiKey, object :
                IHandleAPICallBack<ReversGeoCodeMain> {
                override fun handleWebserviceCallBackSuccess(response: Response<ReversGeoCodeMain>) {
                    geoMarkerSelectedAdress.postValue(
                        ResultWrapper<ReversGeoCodeMain>(
                            Status.status.SUCCESS,
                            response.body(),
                            "Success"
                        )
                    )
                }

                override fun handleWebserviceCallBackFailure(error: String?) {
                    geoMarkerSelectedAdress.postValue(
                        ResultWrapper<ReversGeoCodeMain>(
                            Status.status.ERROR,
                            null,
                            error
                        )
                    )
                }


                override fun onConnectionError() {
                    geoMarkerSelectedAdress.postValue(
                        ResultWrapper<ReversGeoCodeMain>(
                            Status.status.ERROR,
                            null,
                            "Connection Error"
                        )
                    )
                }

                override fun handleWebserviceCallBackFailure(error: Int?) {
                    geoMarkerSelectedAdress.postValue(
                        ResultWrapper<ReversGeoCodeMain>(
                            Status.status.NOREUSLT,
                            null,
                            "NO Address Found"
                        )
                    )

                }


            })
        }
    }

    private fun getJsonEncode(latLng: LatLng): RequestBody? {
        val mainJson = JSONObject()
        val locationJSON = JSONObject()
        locationJSON.put("lng", latLng.longitude)
        locationJSON.put("lat", latLng.latitude)
        mainJson.put("location", locationJSON)
        mainJson.put("language", "en")
        mainJson.put("", "CN")
        return RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            mainJson.toString()
        )
    }

    fun getLocation(activity: Activity) {
        currentLocation.postValue(
            ResultWrapper<Location>(
                Status.status.LOADING,
                null, "Loading"
            )
        )

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)




        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    currentLocation.postValue(
                        ResultWrapper<Location>(
                            Status.status.ERROR,
                            null, "Location Null"
                        )
                    )
                } else {


                    currentLocation.postValue(
                        ResultWrapper<Location>(
                            Status.status.SUCCESS,
                            location, "Success"
                        )
                    )

                }
                // Got last known location. In some rare situations this can be null.
            }
    }

    fun cancelJobs() {
        Repository.cancelJobs()
    }


}