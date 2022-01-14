package com.example.sanic.location

import android.Manifest
import android.content.BroadcastReceiver
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.os.Looper
import android.util.Log
import com.example.sanic.Point
import com.google.android.gms.location.*

class Location(private val context: Context) {
    private val geofenceBroadcastReceiver: BroadcastReceiver? = null
    private var fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
    private var observer: LocationObserver? = null
    fun start(observer: LocationObserver) {
        this.observer = observer
        startLocationUpdates()
    }

    private val locationRequest: LocationRequest
        get() {
            val locationRequest = LocationRequest.create()
            locationRequest.interval = 10000
            locationRequest.fastestInterval = 5000
            locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            return locationRequest
        }

    //Update location to interface
    private val locationCallback: LocationCallback
        get() = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {

                for (location in locationResult.locations) {
                    //Update location to interface
                    observer?.onLocationUpdate(Point(location.latitude, location.longitude, null))
                }
            }
        }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("location", "no permissinos")
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
        Log.d("location", "Starting locationupdates")
    }

    fun stop() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        fusedLocationClient.flushLocations()
    }

    init {
        //PermissionManager permissionManager = new PermissionManager
        //permissionManager.getLocationPermissions()
    }
}