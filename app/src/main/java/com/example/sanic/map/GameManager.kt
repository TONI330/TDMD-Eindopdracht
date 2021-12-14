package com.example.sanic.map

import android.util.Log
import com.example.sanic.Point
import com.example.sanic.location.Location
import com.example.sanic.location.LocationObserver
import com.google.android.gms.location.Geofence
import org.osmdroid.util.GeoPoint
import kotlin.math.log


class GameManager(private val gameActivity: GameActivity, private val randomPointGenerator: RandomPointGenerator) : LocationObserver{

    val checkPoints: ArrayList<Point> = ArrayList()
    val geofenceRadius : Int = 15

    fun startGpsUpdates() {
        generateRandom()
        //TODO get locationmanager to start gps updates
        val location = Location(gameActivity)
        location.start(this)
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

    override fun onLocationError() {
        TODO("Not yet implemented")
    }

    override fun onLocationUpdate(point: Point?) {
        val geoPoint = point?.toGeoPoint()

        if (checkPoints.size == 0)
            return

        val distance = geoPoint?.distanceToAsDouble(checkPoints.last().toGeoPoint())
        Log.d("location", "Distance: $distance")
        if(distance!! <= geofenceRadius) {
            Log.d("location", "Geofence triggered!")
            generateRandom()
        }
    }

    override fun onNearLocationEntered(geofence: Geofence?) {
        TODO("Not yet implemented")
    }


}