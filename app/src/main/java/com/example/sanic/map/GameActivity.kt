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

class GameActivity : AppCompatActivity() {

    private lateinit var lastLocation : Location
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var binding: ActivityGameBinding

    private lateinit var openStreetMap: OSMap
    private lateinit var gameManager: GameManager

    //Settings variables
    //private var pointsUntilGameEnds: Int = 10
    private var instructionsVisible: Boolean = true

    private  val scoreViewModel: ScoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
//        currentLocation()
//        getLastLocation()



    }

    override fun onStart() {
        super.onStart()
        startGame()
    }

    private fun startGame() {
        val volleyRequestHandler = VolleyRequestHandler(this)

        gameManager = GameManager(this,volleyRequestHandler)
        gameManager.start()
        this.openStreetMap = OSMap(binding.map, this, gameManager)
        openStreetMap.start()



        scoreViewModel.getCurrentScore().observe(this, {
            binding.currentScore.text = it.toString()
        })

        scoreViewModel.getHighscore().observe(this, {
            binding.highScore.text = it.toString()
        })

        scoreViewModel.getTimerMillis().observe(this, {
            val totalSeconds = it / 1000
            val minutes: Long = totalSeconds / 60
            val seconds = (totalSeconds % 60)
            val formatedSeconds = String.format("%02d",seconds)
            val timeLeft = "$minutes:$formatedSeconds"
            binding.timeLeft.text = timeLeft
        })
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
    fun gameOver() {
        Log.d("MainActivity", "openGameOver: ")
        val findFragmentByTag = supportFragmentManager.findFragmentByTag("GameOver")
        if (findFragmentByTag != null) {
            onBackPressed()
            return
        }
        supportFragmentManager.commit {
            add<GameOverFragment>(R.id.fragmentContainerView2, "GameOver")
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

    private fun updateSettings() {
        //update instructionsVisible
        if(KeyValueStorage.getValue(this, "showInstructions").equals("true")) {
            this.instructionsVisible = true
            return
        }
        this.instructionsVisible = false

    }

    fun stopGame(view: android.view.View) {
        gameManager.stop()
        finish()
    }

}