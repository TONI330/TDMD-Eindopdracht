package com.example.sanic.location

import android.util.Log
import com.example.sanic.Point
import com.example.sanic.api.RequestHandler
import com.example.sanic.api.ResponseListener
import org.json.JSONObject
import java.util.*


/**
 * Class handles the calculation of a route using the Open Route Service.
 * The calculated route is called back to the RouteCalculatorListener given in the constructor
 */
class RouteCalculator(private val handler : RequestHandler) {

    private val API_KEY = "5b3ce3597851110001cf6248b2d4f0a0d87548159de20a849ed50911"
    private val URL =
        "https://api.openrouteservice.org/v2/directions/foot-walking?api_key=$API_KEY"

    private fun getURL2Points(start: Point, end: Point): String {
        return URL +
                "&start=" + start.lon + "," + start.lat +
                "&end=" + end.lon + "," + end.lat
    }

    /**
     * Given a set of waypoints this method uses the Open Route Service to get a route between these
     * points. A list of Points is returned
     */
    fun calculate(start : Point, end : Point, listener: ResponseListener) {
        val urL2Points = getURL2Points(start, end)
        Log.i("RouteCalculator", "urL2Points: $urL2Points")
        handler.getRequest(urL2Points, listener)
    }
}
