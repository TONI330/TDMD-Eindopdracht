package com.example.sanic.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import com.example.sanic.Point
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.api.IGeoPoint
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class OSMap {


    private var mapManager: GameManager
    private val activity: Activity
    private var context : Context
    private var mapView : MapView
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private lateinit var startingPoint : Point

    constructor(map: MapView, activity: Activity, mapManager: GameManager, startPoint: Point) {
        this.startingPoint = startPoint
        this.mapManager = mapManager
        this.mapView = map
        this.activity = activity
        this.context = activity.applicationContext
    }



    fun start() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        Configuration.getInstance().load(context, context.getSharedPreferences("osm_config", Context.MODE_PRIVATE))
        setupMaps()
    }

    private fun setupMaps() {
        //setCenterToLastLocation()
        mapView.apply{
            setTileSource(TileSourceFactory.MAPNIK)

            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(context), mapView)
            locationOverlay.enableMyLocation()
            overlays.add(locationOverlay)

            controller.apply {
                Log.d("debug", "zooming")
                setZoom(21.0)
                setCenter(startingPoint.toGeoPoint())
            }
        }
    }

    fun drawCheckPoint(point: IGeoPoint) {
        val checkPoint = Marker(mapView)
        checkPoint.position = point as GeoPoint?
        mapView.overlays.add(checkPoint)
    }

    @SuppressLint("MissingPermission")
    private fun setCenterToLastLocation() {
        var locationPoint : IGeoPoint?
        fusedLocationClient!!.lastLocation
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful && task.result != null) {
                    val lastLocation = task.result
                    locationPoint = GeoPoint(lastLocation.latitude, lastLocation.longitude)
                    //this.startingPoint = Point(lastLocation.latitude, lastLocation.longitude, "start")
                    Log.d("debug", "Last location: $locationPoint")
                    mapView.controller.apply {
                        setCenter(locationPoint)
                    }
                } else {
                    Log.w("debug", "getLastLocation:exception" + task.exception.toString())
                }
            }
    }



}