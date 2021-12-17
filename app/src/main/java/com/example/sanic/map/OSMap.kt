package com.example.sanic.map

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
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
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlay
import org.osmdroid.views.overlay.simplefastpoint.SimpleFastPointOverlayOptions
import org.osmdroid.views.overlay.simplefastpoint.SimplePointTheme
import android.graphics.Bitmap

import android.graphics.drawable.BitmapDrawable

import android.graphics.drawable.Drawable

import androidx.core.content.res.ResourcesCompat
import com.example.sanic.R


class OSMap {


    private var mapManager: GameManager
    private val activity: Activity
    private var context : Context
    private var mapView : MapView
    private var fusedLocationClient: FusedLocationProviderClient? = null

    private var checkPoint : Marker? = null
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
        mapView.overlays.apply {



            if(checkPoint != null) remove(checkPoint)

            val marker = Marker(mapView)
            //marker.icon = ContextCompat.getDrawable(context, R.mipmap.geolocation)

            val d = ResourcesCompat.getDrawable(context.resources, R.mipmap.geolocation, null)
            val bitmap = (d as BitmapDrawable?)!!.bitmap
            val dr: Drawable = BitmapDrawable(
                context.resources,
                Bitmap.createScaledBitmap(
                    bitmap,
                    (48.0 * context.resources.getDisplayMetrics().density).toInt(),
                    (48.0 * context.resources.getDisplayMetrics().density).toInt(),
                    true
                )
            )
            marker.icon = dr
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

            marker.position = point as GeoPoint?
            //marker.textLabelBackgroundColor = R.color.common_google_signin_btn_text_dark
            checkPoint = marker
            mapView.overlays.add(marker)
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