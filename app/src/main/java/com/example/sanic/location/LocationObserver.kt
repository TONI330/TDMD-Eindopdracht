package com.example.sanic.location

import com.example.sanic.Point
import com.google.android.gms.location.Geofence

interface LocationObserver {
    fun onLocationError()
    fun onLocationUpdate(point: Point?)
    fun onNearLocationEntered(geofence: Geofence?)
}