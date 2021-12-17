package com.example.sanic.map

import android.util.Log
import com.example.sanic.KeyValueStorage
import com.example.sanic.Point
import com.example.sanic.R
import com.example.sanic.location.Location
import com.example.sanic.location.LocationObserver
import com.google.android.gms.location.Geofence
import kotlin.concurrent.thread
import kotlin.math.log


class GameManager(
    private val gameActivity: GameActivity,
    private val randomPointGenerator: RandomPointGenerator
) : LocationObserver {

    init {
        KeyValueStorage.setValue(gameActivity, R.string.currentscorekey, "0")
    }

    val checkPoints: ArrayList<Point> = ArrayList()
    val geofenceRadius: Int = 15

    fun startGpsUpdates() {
        generateRandom()
        //TODO get locationmanager to start gps updates
        val location = Location(gameActivity)
        location.start(this)
    }

    private fun endGame()
    {
        KeyValueStorage.setValue(gameActivity, R.string.currentscorekey, "0")
    }

    private fun resetGame()
    {
        endGame()
        KeyValueStorage.setValue(gameActivity, R.string.highscorekey, "0")
    }

    private fun setNextCheckPoint() {
        //randomPoint.getRandomPoint(50.0)
    }

    fun checkValid(point: Point): Boolean {
        for (checkPoint in checkPoints) {
            if (point.id.equals(checkPoint.id))
                return false
        }
        return true
    }

    fun tooMuchTries() {
        Log.d("GameManager", "tooMuchTries: ")
    }

    var tries: Int = 0
    fun generateRandom() {
        thread {
            randomPointGenerator.getRandomSnappedPoint(500.0) { point ->
                if (tries > 10) {
                    tries = 0
                    tooMuchTries()
                } else {
                    if (checkValid(point)) {
                        tries = 0
                        checkPoints.add(point)
                        gameActivity.drawPointOnMap(point)
                    } else {
                        tries++
                        generateRandom()
                    }
                }
            }
        }
    }

    override fun onLocationError() {
        TODO("Not yet implemented")
    }

    override fun onLocationUpdate(point: Point?) {
        val geoPoint = point?.toGeoPoint()

        if (checkPoints.size == 0)
            return

        val distance = geoPoint?.distanceToAsDouble(checkPoints.last().toGeoPoint())
        Log.d("location", "Distance: $distance")
        if (distance!! <= geofenceRadius) {
            Log.d("location", "Geofence triggered!")
            updateScore()
            generateRandom()
        }
    }


    private fun updateScore() {
        var highScore = KeyValueStorage.getValue(gameActivity, R.string.highscorekey)
        if (highScore == null || highScore.isEmpty())
            highScore = "0"

        var currentScore = KeyValueStorage.getValue(gameActivity, R.string.currentscorekey)
        if (currentScore == null || currentScore.isEmpty())
            currentScore = "0"

        val intHighScore = highScore.toInt()
        var intCurrentScore = currentScore.toInt()
        intCurrentScore++

        if (intCurrentScore > intHighScore) {
            KeyValueStorage.setValue(
                gameActivity,
                R.string.highscorekey,
                intCurrentScore.toString()
            )
            Log.i("GameManager", "updateScore new HighScore: $currentScore")
        }

        KeyValueStorage.setValue(gameActivity, R.string.currentscorekey, intCurrentScore.toString())
        Log.i("GameManager", "updateScore new CurrentScore: $currentScore")

    }

    override fun onNearLocationEntered(geofence: Geofence?) {
        TODO("Not yet implemented")
    }


}