package com.huaweiHMSdemo.huawei.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.huawei.hms.maps.*
import com.huawei.hms.maps.HuaweiMap.OnMarkerDragListener
import com.huawei.hms.maps.model.*
import com.huaweiHMSdemo.huawei.R
import com.huaweiHMSdemo.huawei.utils.Status
import com.huaweiHMSdemo.huawei.viewmodel.MainViewModel


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    lateinit var huaweiMap: HuaweiMap
    private var mMarker: Marker? = null
    private var clickMarker: Marker? = null
    private var mMapFragment: MapFragment? = null

    private val COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION
    private val FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION
    private val LOCATION_PERMISSION_CODE = 1234
    private var mLocationPermissions = false
    private var mainViewModel: MainViewModel? = null
    lateinit var curentLatLng: LatLng
    lateinit var clickLatlon: LatLng

    var zoom = 12.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mMapFragment =
            fragmentManager.findFragmentById(R.id.mapfragment_mapfragmentdemo) as MapFragment
        mMapFragment?.getMapAsync(this)

        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        mainViewModel?.geoCodedAdressCurrentLocation?.observe(this, Observer {
            when (it.status) {
                Status.status.SUCCESS -> {
//                    Toast.makeText(this, it.data.sites[0].formatAddress, Toast.LENGTH_SHORT).show()
                    UpdateCamera(curentLatLng)
                    addMarker(curentLatLng, it.data.sites[0].formatAddress)
                    mainViewModel?.getMarkerAddressFromLatLong(clickLatlon)

                }
                Status.status.ERROR -> {

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                }
                Status.status.LOADING -> {

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                }
                Status.status.NOREUSLT -> {
                    UpdateCamera(curentLatLng)
                    addMarker(curentLatLng, it.message)
                }

            }
        })
        mainViewModel?.geoMarkerSelectedAdress?.observe(this, Observer {
            when (it.status) {
                Status.status.SUCCESS -> {
//                    Toast.makeText(this, it.data.sites[0].formatAddress, Toast.LENGTH_SHORT).show()
                    addClickMarker(clickLatlon, it.data.sites[0].formatAddress)
                    UpdateCamera(clickLatlon)
                }
                Status.status.ERROR -> {

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                }
                Status.status.LOADING -> {

                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                }
                Status.status.NOREUSLT -> {
                    addClickMarker(clickLatlon, it.message)
                    UpdateCamera(clickLatlon)
                }
            }
        })

    }

    override fun onMapReady(p0: HuaweiMap?) {
        if (p0 != null) {
            goToCurrentLocation()
            huaweiMap = p0
            huaweiMap.isMyLocationEnabled = true// Enable the my-location overlay.
            huaweiMap.uiSettings.isMyLocationButtonEnabled = true// Enable the my-location icon.
            huaweiMap.mapType = HuaweiMap.MAP_TYPE_NORMAL
            huaweiMap.setOnMapClickListener { latLng ->
                clickLatlon = latLng
                mainViewModel?.getMarkerAddressFromLatLong(clickLatlon)
            }
        }

    }

    fun goToCurrentLocation() {
        if (mLocationPermissions) {


            mainViewModel?.getLocation(this)
            mainViewModel?.currentLocation?.observe(this, Observer {
                when (it.status) {
                    Status.status.SUCCESS -> {

                        curentLatLng = LatLng(it.data.latitude, it.data.longitude)
                        clickLatlon = LatLng(it.data.latitude +0.04, it.data.longitude)
                        mainViewModel?.getCurrentAddressFromLatLong(curentLatLng)
                        UpdateCamera(curentLatLng)
                    }
                    Status.status.ERROR -> {

                        statusCheck()

                    }
                    Status.status.LOADING -> {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()

                    }
                    Status.status.NOREUSLT -> {

                    }

                }
            })
        } else {
            getLoctionPermissions()
        }
    }

    fun statusCheck() {
        val manager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    fun buildAlertMessageNoGps() {
        val builder =
            AlertDialog.Builder(this)
        builder.setMessage("Your GPS seems to be disabled, Please Enable it?")
            .setCancelable(false)
            .setPositiveButton(
                "Ok"
            ) { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
        val alert = builder.create()
        alert.show()
    }

    fun UpdateCamera(latLng: LatLng) {
        val cameraUpdate5: CameraUpdate =
            CameraUpdateFactory.newLatLngZoom(latLng, zoom)
        huaweiMap.animateCamera(cameraUpdate5)
    }

    fun addMarker(latLng: LatLng, tile: String) {
        mMarker?.remove()
        val options = MarkerOptions()
            .position(latLng)
            .icon(BitmapDescriptorFactory.fromResource(R.drawable.star))
            .title(tile)
        mMarker = huaweiMap.addMarker(options)

    }

    fun addClickMarker(latLng: LatLng, tile: String) {
        clickMarker?.remove()
        val options = MarkerOptions()
            .position(latLng)
            .title(tile)
        clickMarker = huaweiMap.addMarker(options)
        clickMarker!!.isDraggable = true
        huaweiMap.setOnMarkerDragListener(object : OnMarkerDragListener {
            override fun onMarkerDragStart(marker: Marker) {


            }

            override fun onMarkerDrag(marker: Marker) {

            }

            override fun onMarkerDragEnd(marker: Marker) {
                clickLatlon = LatLng(marker.position.latitude, marker.position.longitude)
                mainViewModel?.getMarkerAddressFromLatLong(clickLatlon)
            }
        })
    }

    ///////////Permissions////////////////////////
    private fun getLoctionPermissions() {
        val permission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        if (ContextCompat.checkSelfPermission(
                this@MainActivity.applicationContext,
                FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity.applicationContext,
                    COURSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mLocationPermissions = true
                // goToCurrentLocation();
                goToCurrentLocation()
            } else {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    permission,
                    LOCATION_PERMISSION_CODE
                )
            }
        } else {
            ActivityCompat.requestPermissions(
                this@MainActivity,
                permission,
                LOCATION_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mLocationPermissions = false
        when (requestCode) {
            LOCATION_PERMISSION_CODE -> {
                if (grantResults.size > 0) {
                    var i = 0
                    while (i < grantResults.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissions = false
                        } else {
                            mLocationPermissions = true
                            //getLocation
                            goToCurrentLocation()
                        }
                        i++
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainViewModel?.cancelJobs()
    }
}
