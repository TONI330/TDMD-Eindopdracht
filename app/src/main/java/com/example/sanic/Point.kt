package com.example.sanic

import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint

data class Point(val lat: Double, val lon: Double, val id: String?) {
    fun toGeoPoint(): IGeoPoint {
        return GeoPoint(lat, lon)
    }
}