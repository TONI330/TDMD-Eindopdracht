package com.example.sanic.map

import android.util.Log
import com.example.sanic.KeyValueStorage
import com.example.sanic.Point
import com.example.sanic.R
import com.example.sanic.api.PhotonApiManager
import com.example.sanic.api.ResponseListener
import com.example.sanic.api.VolleyRequestHandler
import com.example.sanic.location.Location
import com.example.sanic.location.LocationObserver
import com.example.sanic.location.RouteCalculator
import com.google.android.gms.location.Geofence
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.concurrent.thread
import kotlin.math.ceil


class GameManager(
    private val gameActivity: GameActivity,
    private val volleyRequestHandler: VolleyRequestHandler
) : LocationObserver, PointListener, ResponseListener {

    //Settings variables
    private var distance: Int = 500

    private val scoreManager: ScoreManager = ScoreManager(gameActivity)
    private val checkPoints: ArrayList<Point> = ArrayList()
    private val geofenceRadius: Int = 15
    private lateinit var currentLocation: Point
    private val timeManager = TimeManager(gameActivity)
    private var gameOver = false
    private var rndPointGen: RandomPointGenerator? = null
    private val routeCalculator: RouteCalculator = RouteCalculator(volleyRequestHandler)
    private var firstLocationRecieved: Boolean = false
    private val location = Location(gameActivity)


    fun start() {
        startGpsUpdates()
    }

    private fun startGpsUpdates() {
        //TODO get locationmanager to start gps updates
        location.start(this)
        //resetGame()
    }


    private fun resetGame() {
        KeyValueStorage.setValue(gameActivity, R.string.highscorekey, "0")
    }


    private fun checkValid(point: Point): Boolean {
        for (checkPoint in checkPoints) {
            if (point.id.equals(checkPoint.id))
                return false
        }
        return true
    }

    private fun tooMuchTries() {
        Log.d("GameManager", "tooMuchTries: ")
    }


    fun generateRandom() {
        val value = KeyValueStorage.getValue(gameActivity.baseContext, "distance")
        if (value != null) {
            this.distance = value.toInt()
        }
        thread {
            rndPointGen?.getRandomSnappedPoint(distance.toDouble(), this)
        }
    }
    private var tries: Int = 0
    override fun onPointFound(point: Point) {
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
        if (currentLocation != null) routeCalculator.calculate(currentLocation, point, this)
        else routeCalculator.calculate(gameActivity.getLastLocationAsPoint(), point, this)
    }

    private fun setDistanceToNextPoint(distanceInMeters: Double) {
        // a part is 100 meter
        val parts = ceil(distanceInMeters / 100.0).toInt()
        Log.d("GameManger", "DistanceToNextPoint: $distanceInMeters")
        Log.d("GameManger", "Pats: $parts")

        val value = KeyValueStorage.getValue(gameActivity.baseContext, "secondsMultiplier")
        val timePerPart = value!!.toInt()

        val time: Int = parts * timePerPart

        setTimer(time, ::timerTriggered)
    }

    private fun timerTriggered() {
        gameOver = true
        gameActivity.gameOver()
        Log.d("testing", "timerTriggered: ")
    }


    private fun setTimer(delayInSeconds: Int, task: () -> Unit) {
        var delayInMillis = delayInSeconds.toLong()
        delayInMillis *= 1000L
        timeManager.setTimer(delayInMillis, task)
    }

    override fun onResponse(response: JSONObject) {

        // Try parsing the data to JSON
        try {
            // Getting the nested JSON array coordinates
            val feature = response
                .getJSONArray("features")
                .getJSONObject(0)

            val distance = feature
                .getJSONObject("properties")
                .getJSONObject("summary")
                .getDouble("distance")
            setDistanceToNextPoint(distance)


            val coordinatesObject = feature
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


    fun onFirstLocationUpdate(point: Point) {
        rndPointGen = RandomPointGenerator(point, PhotonApiManager(volleyRequestHandler))
        gameActivity.getMap().setStartingPoint(point)
        generateRandom()
    }


    override fun onLocationUpdate(point: Point?) {
        if (gameOver) {
            location.stop()
            return
        }


        if (point == null) return

        if (!firstLocationRecieved) {
            onFirstLocationUpdate(point)
            firstLocationRecieved = true
        }


        currentLocation = point

        val geoPoint = point.toGeoPoint()

        if (checkPoints.size == 0)
            return

        val distance = geoPoint.distanceToAsDouble(checkPoints.last().toGeoPoint())
        Log.d("location", "Distance: $distance")
        if (distance <= geofenceRadius) {
            Log.d("location", "Geofence triggered!")
            scoreManager.updateScore()
            generateRandom()
        }
    }


    override fun onNearLocationEntered(geofence: Geofence?) {
        TODO("Not yet implemented")
    }

    fun stop() {
        timeManager.cancelTimer()
    }


}