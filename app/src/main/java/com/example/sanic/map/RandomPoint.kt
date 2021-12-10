package com.example.sanic.map

import com.example.sanic.Point
import kotlin.math.cos

class RandomPoint(private val startingPoint: Point) {

    fun getRandomPoint(radius: Double) : Point {
        var lat : Double = startingPoint.lat + 2 * ((Math.random() * metersToLat(radius) * 2) - metersToLat(radius))
        var lon : Double = startingPoint.lon + 2 * ((Math.random() * metersToLon(radius, startingPoint.lat) * 2) - metersToLon(radius, startingPoint.lat))

        lat = Math.round(lat * 10000000) / 10000000.0
        lon = Math.round(lon * 10000000) / 10000000.0

        return Point(lat, lon, "random")
    }

    private fun metersToLat(meters : Double) : Double {
        return meters / 111320
    }

    private fun metersToLon(meters: Double, lat : Double): Double {
        return meters / (40075000 * (cos(lat) / 360))
    }

}
