package com.example.sanic.map

import android.util.Log
import androidx.activity.viewModels
import com.example.sanic.KeyValueStorage
import com.example.sanic.Point
import com.example.sanic.R
import com.example.sanic.ScoreViewModel
import com.example.sanic.api.ResponseListener
import com.example.sanic.location.Location
import com.example.sanic.location.LocationObserver
import com.example.sanic.location.RouteCalculator
import com.google.android.gms.location.Geofence
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.log


class GameManager(private val gameActivity: GameActivity, private val randomPointGenerator: RandomPointGenerator, private val routeCalculator: RouteCalculator) : LocationObserver, PointListener, ResponseListener {

    private  val scoreViewModel: ScoreViewModel by gameActivity.viewModels()
    private val checkPoints: ArrayList<Point> = ArrayList()
    private val geofenceRadius: Int = 15
    private var currentLocation : Point? = null

    fun start()
    {
        scoreViewModel.getHighscore().observe(gameActivity, {
            KeyValueStorage.setValue(gameActivity, R.string.highscorekey, "$it")
        })
        startGpsUpdates()
    }


    fun startGpsUpdates() {
        generateRandom()
        //TODO get locationmanager to start gps updates
        val location = Location(gameActivity)
        location.start(this)
        //resetGame()
    }


    private fun resetGame()
    {
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
            randomPointGenerator.getRandomSnappedPoint(500.0, this)
        }
    }

    override fun onPointFound(point : Point) {
            if (tries > 10) {
                tries = 0
                tooMuchTries()
            } else {
                if (checkValid(point)) {
                    tries = 0
                    onPointSucces(point)
                } else {
                    tries++
                    generateRandom()
                }
            }
    }

    private fun onPointSucces(point: Point) {
        checkPoints.add(point)
        gameActivity.drawPointOnMap(point)
        if(currentLocation != null) routeCalculator.calculate(currentLocation!!, point, this)
        else routeCalculator.calculate(gameActivity.getLastLocationAsPoint(), point, this)
    }

    override fun onResponse(response: JSONObject) {

        // Try parsing the data to JSON
        try {
            // Getting the nested JSON array coordinates
            val coordinatesObject = response
                .getJSONArray("features")
                .getJSONObject(0)
                .getJSONObject("geometry")
                .getJSONArray("coordinates")

            // Creating the list of GeoPoints from the JSON array given
            val routePoints = JSONtoPointList(coordinatesObject)

            // Returning the value's to the listener
            gameActivity.getMap().drawRoute(routePoints)
            //listener.onRoutePointsCalculated(routePoints)
        } catch (e: JSONException) {
            // Informing the listener of the error
        }
    }

    private fun JSONtoPointList(coordinates: JSONArray): List<Point> {
    val points: MutableList<Point> = ArrayList()
    for (i in 0 until coordinates.length()) {
        val coordinateArray = coordinates.optJSONArray(i) ?: continue

        // Checking if coordinateArray in not null

        // Getting the longitude and latitude
        val longitude = coordinateArray.optDouble(0, 91.0)
        val latitude = coordinateArray.optDouble(1, 91.0)

        // Checking if the value's are valid (they can never go over 90)
        if (longitude == 91.0 || latitude == 91.0) continue

        // Creating a GeoPoint based on the latitude and longitude and adding it to the list
        points.add(Point(latitude, longitude, "route-point"))
    }

        // Returning the new list
        return points
    }

    override fun onLocationError() {
        TODO("Not yet implemented")
    }

    override fun onLocationUpdate(point: Point?) {
        if (point != null) {
            currentLocation = point
        }

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
        scoreViewModel.updateCurrentScore()
    }

    override fun onNearLocationEntered(geofence: Geofence?) {
        TODO("Not yet implemented")
    }


}