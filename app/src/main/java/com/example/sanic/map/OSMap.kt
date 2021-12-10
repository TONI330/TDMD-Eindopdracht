package com.example.sanic.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class OSMap {


    private val activity: Activity
    private var context : Context
    private var map : MapView
    private var fusedLocationClient: FusedLocationProviderClient? = null

    constructor(map: MapView, activity: Activity) {
        this.map = map
        this.activity = activity
        this.context = activity.applicationContext
    }



    public fun start() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_config", Context.MODE_PRIVATE))
        setupMaps()
    }

    private fun setupMaps() {
        setCenterToLastLocation()
        map.apply{
            setTileSource(TileSourceFactory.MAPNIK)

            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), map)
            locationOverlay.enableMyLocation()
            overlays.add(locationOverlay)

            controller.apply {
                Log.d("debug", "zooming")
                setZoom(21.0)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setCenterToLastLocation() {
        var locationPoint : IGeoPoint?
        fusedLocationClient!!.lastLocation
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful && task.result != null) {
                    val lastLocation = task.result
                    locationPoint = GeoPoint(lastLocation.latitude, lastLocation.longitude)
                    Log.d("debug", "Last location: $locationPoint")
                    map.controller.apply {
                        setCenter(locationPoint)
                    }
                } else {
                    Log.w("debug", "getLastLocation:exception" + task.exception.toString())
                }
            }
    }

}