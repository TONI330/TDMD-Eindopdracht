package com.example.sanic

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import com.example.sanic.databinding.ActivityGameBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class GameActivity : AppCompatActivity() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(LayoutInflater.from(this))
        setContentView(R.layout.activity_game)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        Configuration.getInstance().load(this, getSharedPreferences("osm_config", Context.MODE_PRIVATE))
        getLastLocation()
        setupMaps()
    }

    private fun setupMaps() {

        binding.map.apply{
            setTileSource(TileSourceFactory.MAPNIK)

            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this@GameActivity), this)
            locationOverlay.enableMyLocation()
            overlays.add(locationOverlay)

            controller.apply {
//                val currentLocation = currentLocation()
//                setCenter(currentLocation)
                setZoom(6.0)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
            fusedLocationClient!!.lastLocation
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful && task.result != null) {
                        val mLastLocation = task.result
                        Log.d("debug", mLastLocation.toString())
                    } else {
                        Log.w("debug", "getLastLocation:exception", task.exception)
                    }
                }
    }

    @Nullable
    private fun currentLocation() : GeoPoint? {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d("debug", "no permissions")

            return null
        }
        Log.d("debug", "getting location..")
        val token = CancellationTokenSource()
            val currentLocation = fusedLocationClient?.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token.token)
            val locationPoint = currentLocation?.result?.let { GeoPoint(it.latitude, currentLocation.result.longitude) }
        Log.d("debug", locationPoint.toString())
        return locationPoint
    }

}