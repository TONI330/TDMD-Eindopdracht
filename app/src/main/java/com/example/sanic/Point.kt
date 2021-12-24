package com.example.sanic

import org.osmdroid.util.GeoPoint

data class Point(val lat: Double, val lon: Double, val id: String?) {

    fun toGeoPoint(): GeoPoint {
        return GeoPoint(lat, lon)
    }
}