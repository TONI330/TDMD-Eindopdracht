package com.example.sanic.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import com.example.sanic.Point
import com.example.sanic.api.PhotonApiManager
import com.example.sanic.api.VolleyRequestHandler
import com.example.sanic.databinding.ActivityGameBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint

class GameActivity : AppCompatActivity() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityGameBinding

    private lateinit var openStreetMap: OSMap
    private lateinit var gameManager: GameManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
    }

    private fun startGame(startPoint: Point) {
        val rndPointGen = RandomPointGenerator(startPoint, PhotonApiManager(VolleyRequestHandler(this)))
        gameManager = GameManager(this, rndPointGen)
        this.openStreetMap = OSMap(binding.map, this, gameManager, startPoint)
        openStreetMap.start()
    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val lastLocation = task.result
                    startGame(Point(lastLocation.latitude, lastLocation.longitude, "start"))
                } else {
                    Log.w("debug", "getLastLocation:exception" + task.exception.toString())
                }
            }
    }

    fun stopGame(view: View) {
        finish()
    }

    @Nullable
    private fun currentLocation(): IGeoPoint? {
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
        var locationPoint: IGeoPoint? = null
        fusedLocationClient?.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY, token.token)
            ?.addOnSuccessListener { location ->
                locationPoint = GeoPoint(location.latitude, location.longitude)
            }
        //Log.d("debug", "Exception: " + currentLocation?.exception)

        //val locationPoint = currentLocation?.result?.let { GeoPoint(it.latitude, currentLocation.result.longitude) }
        Log.d("debug", "Current location: " + locationPoint.toString())
        return locationPoint
    }


    fun drawPointOnMap(point: Point) {
        point.toGeoPoint().run {
            openStreetMap.drawCheckPoint(this)
            Log.d("random", "Point found: $this")
        }
    }

    fun generateRandom(view: View) {
        gameManager.generateRandom()
    }


}