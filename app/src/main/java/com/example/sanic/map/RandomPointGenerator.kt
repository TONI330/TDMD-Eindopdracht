package com.example.sanic.map

import com.example.sanic.Point
import com.example.sanic.api.PhotonApiManager
import com.example.sanic.api.StreetListener
import kotlin.math.cos
import kotlin.math.roundToLong

class RandomPointGenerator(val startingPoint: Point, val api : PhotonApiManager?) {

//    private var api: PhotonApiManager? = null
//    private var startingPoint: Point
//
//    constructor(startingPoint: Point, api : PhotonApiManager) {
//        this.startingPoint = startingPoint
//        this.api = api
//
//    }
//
//    constructor(startingPoint: Point) {
//        this.startingPoint = startingPoint
//    }

    fun getRandomPoint(radius: Double) : Point {
        var lat : Double = startingPoint.lat + ((Math.random() * (metersToLat(radius) * 2)) - metersToLat(radius))
        var lon : Double = startingPoint.lon + ((Math.random() * (metersToLon(radius, startingPoint.lat) * 2)) - metersToLon(radius, startingPoint.lat))

        lat = (lat * 10000000).roundToLong() / 10000000.0
        lon = (lon * 10000000).roundToLong() / 10000000.0
        return Point(lat, lon, "random")
    }

    fun getRandomSnappedPoint(radius: Double, pointListener : PointListener) {
        val randomPoint = getRandomPoint(radius)

        api?.getClosestStreet(randomPoint,
            StreetListener { point ->
                if (point == null) {
                    getRandomSnappedPoint(radius, pointListener)
                    return@StreetListener
                }
                pointListener.onPointFound(point)
            })
    }

    private fun metersToLat(meters : Double) : Double {
        return meters / 111320
    }

    private fun metersToLon(meters: Double, lat : Double): Double {
        return meters / (40075000 * (cos(Math.toRadians(lat)) / 360))
    }

}
