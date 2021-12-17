package com.example.sanic.map

import android.app.IntentService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import com.example.sanic.Point
import com.example.sanic.location.geofence.NearLocationManager
import com.example.sanic.location.gps.GeofenceBroadcastReceiver
import com.example.sanic.location.gps.Location
import com.example.sanic.location.gps.LocationObserver
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent


class GameManager(private val gameActivity: GameActivity, private val randomPointGenerator: RandomPointGenerator) :
    LocationObserver {

    var geofenceBroadcastReceiver : BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            //observer?.onNearLocationEntered(GeofencingEvent.fromIntent(intent!!).triggeringGeofences[0])
            Log.d(GeofenceBroadcastReceiver.LOGTAG, "Received a trigger with intent: $intent")
            val geofencingEvent = GeofencingEvent.fromIntent(intent!!)
            if (geofencingEvent.hasError()) {
                Log.d(GeofenceBroadcastReceiver.LOGTAG, "Geofence error:" + geofencingEvent.errorCode)
            }
            val geofence = GeofencingEvent.fromIntent(intent!!).triggeringGeofences[0]
            Log.d("GameManager", "Geofence triggered! " + geofence?.requestId)
            setNewRandCheckPoint()
        }
    }

    var checkPoint: Point? = null
    val geofenceRadius : Int = 15
    val nearLocationManager : NearLocationManager =
        NearLocationManager(gameActivity, geofenceBroadcastReceiver)

    fun startGpsUpdates() {
        val intentFilter = IntentFilter()


        setNewRandCheckPoint()
        //TODO get locationmanager to start gps updates
        val location = Location(gameActivity)
        location.start(this)
    }

    private fun setNextCheckPoint(point: Point) {
        nearLocationManager.setNextNearLocation(point, 80.0)
    }

    fun checkValid(point: Point): Boolean {
        if (point.id.equals(checkPoint?.id)) return false
        return true
    }

    fun tooManyTries() {
        Log.d("GameManager", "Too many tries!")
    }

    var tries: Int = 0
    fun setNewRandCheckPoint() {
        randomPointGenerator.getRandomSnappedPoint(500.0) { point ->
            if (tries > 10) {
                tries = 0
                tooManyTries()
            } else {
                if (checkValid(point)) {
                    tries = 0
                    checkPoint = null
                    checkPoint = point
                    setNextCheckPoint(point)
                    Log.d("GameManager", "Setting new geofence..")
                    //TODO delete last marker
                    gameActivity.drawPointOnMap(point)
                } else {
                    tries++
                    setNewRandCheckPoint()
                }
            }
        }
    }

    override fun onLocationError() {
        TODO("Not yet implemented")
    }

    override fun onLocationUpdate(point: Point?) {
        if(checkPoint == null) {
            Log.w("GameManager", "No active geopoints")
            return
        }
        //Checking if geofence is triggered
        val geoPoint = point?.toGeoPoint()
        val distance = geoPoint?.distanceToAsDouble(checkPoint?.toGeoPoint())
        Log.d("location", "Distance: $distance")
        if(distance!! <= geofenceRadius) {
            //Log.d("location", "Geofence triggered!")
            //generateRandom()
        }
    }

    override fun onNearLocationEntered(geofence: Geofence?) {
        Log.d("GameManager", "Geofence triggered! " + geofence?.requestId)
    }

}