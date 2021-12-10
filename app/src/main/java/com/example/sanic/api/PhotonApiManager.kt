package com.example.sanic.api

import android.util.Log
import com.example.sanic.Point
import org.json.JSONObject

class PhotonApiManager(private val requestHandler: RequestHandler) {

    fun getClosestStreet(point: Point, streetListener: StreetListener) {

        val url = "https://photon.komoot.io/reverse?lat=${point.lat}&lon=${point.lon}"
        val responseListener = object : ResponseListener {
            override fun onResponse(response: JSONObject) {
                getValidLocation(response,streetListener)
            }
        }

        requestHandler.doRequest(url, responseListener)
    }

    private fun getValidLocation(response: JSONObject, streetListener: StreetListener)
    {
        val validLocation = checkValidLocation(response)
        val extractLocation = extractLocation(response)
        if (validLocation) {
            streetListener.onStreetFound(extractLocation)
            return
        }else
        {
            val extractStreetName = extractStreetName(response)
            if (extractStreetName != null) {
                getPointFromStreetName(streetListener, extractStreetName, extractLocation)
                return
            }
            val extractExtendLocation = extractExtendLocation(response)

            if (extractExtendLocation == null) {
                streetListener.onStreetFound(null)
                return
            }
            getClosestStreet(extractExtendLocation,streetListener)

        }
    }




    private var lastPoint: Point = Point(0.0,0.0,"0")
    private fun extractExtendLocation(jsonObject: JSONObject) : Point?
    {
        val extractProperties = extractFirstProperties(jsonObject)
        if (extractProperties == null || !extractProperties.has("extent")) {
            return null
        }
        val coordinatesArray = extractProperties.getJSONArray("extent")

        val lat = (coordinatesArray.getDouble(1) + coordinatesArray.getDouble(3)) / 2.0
        val lon = (coordinatesArray.getDouble(0) + coordinatesArray.getDouble(2)) / 2.0
        val point = Point(lat, lon, "0")

        if (point.lat == lastPoint.lat && point.lon == lastPoint.lon)
            return null

        lastPoint = point

        Log.i("LocationFound", "bullshit $point")
        return point
    }

    private fun extractedType(response: JSONObject): String?
    {
        val jsonObject = extractFirstProperties(response) ?: return null
        if (!jsonObject.has("type")) {
            return null
        }
        return jsonObject.getString("type")
    }


    private fun checkValidLocation(response: JSONObject): Boolean {
        val type = extractedType(response) ?: return false

        val validLocation = type.equals("street")
        Log.i("PhotonApiManager", "onResponse: $response valid: $validLocation")
        return validLocation
    }

    private fun extractFirstProperties(jsonObject: JSONObject): JSONObject? {
        val firstFeature = extractFirstFeature(jsonObject) ?: return null
        return firstFeature.getJSONObject("properties")
    }


    private fun extractFirstFeature(jsonObject: JSONObject): JSONObject? {
        val featuresArray = jsonObject.getJSONArray("features")
        if (featuresArray.length() == 0)
            return null

        return featuresArray.getJSONObject(0)
    }

    private fun extractLocation(jsonObject: JSONObject): Point? {
        val firstFeature = extractFirstFeature(jsonObject) ?: return null

        val geometryObject = firstFeature.getJSONObject("geometry")
        val coordinatesArray = geometryObject.getJSONArray("coordinates")
        //heb index 0 en 1 omgewisseld omdat lat en long omgedraaid waren, xx Teun
        return Point(coordinatesArray.getDouble(1), coordinatesArray.getDouble(0), "0")
    }

    private fun extractStreetName(jsonObject: JSONObject): String? {
        val extractedProperties = extractFirstProperties(jsonObject) ?: return null

        if (extractedProperties.has("street"))
            return extractedProperties.getString("street")

        if (extractedProperties.has("name") && !extractedProperties.has("extent"))
            return extractedProperties.getString("name")

        return null
    }


    private fun getPointFromStreetName(streetListener: StreetListener, streetName: String?, point: Point?) {
        val url: String
        if (point == null)
            url = "https://photon.komoot.io/api?q=$streetName&osm_tag=highway"
        else
            url = "https://photon.komoot.io/api?q=$streetName&lat=${point.lat}&lon=${point.lon}&osm_tag=highway"

        val responseListener = object : ResponseListener {
            override fun onResponse(response: JSONObject) {
                streetListener.onStreetFound(extractLocation(response))
            }
        }
        requestHandler.doRequest(url, responseListener)
    }

}






