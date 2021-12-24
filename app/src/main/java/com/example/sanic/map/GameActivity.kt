package com.example.sanic.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.Nullable
import androidx.core.app.ActivityCompat
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.lifecycle.Observer
import com.example.sanic.*
import com.example.sanic.api.PhotonApiManager
import com.example.sanic.api.VolleyRequestHandler
import com.example.sanic.databinding.ActivityGameBinding
import com.example.sanic.location.RouteCalculator
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationTokenSource
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint
import java.util.*

class GameActivity : AppCompatActivity() {

    private lateinit var lastLocation : Location
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityGameBinding

    private lateinit var openStreetMap: OSMap
    private lateinit var gameManager: GameManager

    private var pointsUntilGameEnds: Int = 10

    private  val scoreViewModel: ScoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        currentLocation()
        getLastLocation()


    }

    override fun onStart() {
        super.onStart()
        pointsUntilGameEnds = KeyValueStorage.getValue(this, "amountOfPoints")?.toInt() ?: 10
        Log.d("GameActivity", "onCreate: $pointsUntilGameEnds")
    }

    private fun startGame(startPoint: Point) {
        val volleyRequestHandler = VolleyRequestHandler(this)
        val rndPointGen = RandomPointGenerator(startPoint, PhotonApiManager(volleyRequestHandler))
        gameManager = GameManager(this, rndPointGen, RouteCalculator(volleyRequestHandler))
        gameManager.start()
        this.openStreetMap = OSMap(binding.map, this, gameManager, startPoint)
        openStreetMap.start()



        scoreViewModel.getCurrentScore().observe(this, {
            binding.currentScore.text = it.toString()
        })

        scoreViewModel.getHighscore().observe(this, {
            binding.highScore.text = it.toString()
        })


    }


    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient!!.lastLocation
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    lastLocation = task.result
                    startGame(Point(lastLocation.latitude, lastLocation.longitude, "start"))
                } else {
                    Log.w("debug", "getLastLocation:exception" + task.exception.toString())
                }
            }
    }

    fun stopGame(view: View) {
        finish()
    }

    @SuppressLint("MissingPermission")
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
        runOnUiThread {
            point.toGeoPoint().run {
                openStreetMap.drawCheckPoint(this)
                Log.d("random", "Point found: $this")
            }
        }
    }

    fun generateRandom(view: View) {
        gameManager.generateRandom()
    }

    fun openSettings(view: View) {
        Log.d("MainActivity", "openSettingMain: ")
        val findFragmentByTag = supportFragmentManager.findFragmentByTag("Settings")
        if (findFragmentByTag != null) {
            onBackPressed()
            return
        }
        supportFragmentManager.commit {
            add<SettingsFragment>(R.id.fragmentContainerView2, "Settings")
            setReorderingAllowed(true)
            addToBackStack("null")
        }
    }

    fun getMap() : OSMap {
        return this.openStreetMap
    }

    fun getLastLocationAsPoint() : Point {
        return Point(lastLocation.latitude, lastLocation.longitude, "lastLocation")
    }



}