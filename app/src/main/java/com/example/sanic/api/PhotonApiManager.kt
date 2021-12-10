package com.example.sanic.api

import android.util.Log
import com.example.sanic.Point
import org.json.JSONObject

class PhotonApiManager(private val requestHandler: RequestHandler) {

    fun getClosestStreet(point: Point, streetlistener: Streetlistener) {

        val url = "https://photon.komoot.io/reverse?lat=${point.lat}&lon=${point.lon}"
        val responseListener = object : ResponseListener {
            override fun onResponse(response: JSONObject) {
                getValidLocation(response,streetlistener)
            }
        }

        requestHandler.doRequest(url, responseListener)
    }

    private fun getValidLocation(response: JSONObject, streetlistener: Streetlistener)
    {
        val validLocation = checkValidLocation(response)
        val extractLocation = extractLocation(response)
        if (validLocation) {
            streetlistener.onStreetFound(extractLocation)
            return
        }else
        {
            val extractStreetName = extractStreetName(response)
            if (extractStreetName != null) {
                getPointFromStreetName(streetlistener, extractStreetName, extractLocation)
                return
            }
            getClosestStreet(extractLocation,streetlistener)
        }
    }



    private fun checkValidLocation(response: JSONObject): Boolean {
        val extractStreetName = extractStreetName(response)
        val validLocation = extractStreetName.equals("street")
        Log.i("PhotonApiManager", "onResponse: $response valid: $validLocation")
        return validLocation
    }

    private fun extractFirstProperties(jsonObject: JSONObject): JSONObject {
        val firstFeature = extractFirstFeature(jsonObject)
        return firstFeature.getJSONObject("properties")
    }


    private fun extractFirstFeature(jsonObject: JSONObject): JSONObject {
        val featuresArray = jsonObject.getJSONArray("features")
        return featuresArray.getJSONObject(0)
    }

    private fun extractLocation(jsonObject: JSONObject): Point {
        val firstFeature = extractFirstFeature(jsonObject)

        val geometryObject = firstFeature.getJSONObject("geometry")
        val coordinatesArray = geometryObject.getJSONArray("coordinates")

        return Point(coordinatesArray.getDouble(0), coordinatesArray.getDouble(1), "0")
    }

    private fun extractStreetName(jsonObject: JSONObject): String? {
        val extractedProperties = extractFirstProperties(jsonObject)
        if (!extractedProperties.has("street")) {
            return null
        }
        return extractedProperties.getString("street")
    }


    private fun getPointFromStreetName(streetlistener: Streetlistener, streetName: String?, point: Point?) {
        val url: String
        if (point == null)
            url = "http://photon.komoot.io/api?q=$streetName&osm_tag=highway"
        else
            url =
                "http://photon.komoot.io/api?q=$streetName&lat=${point.lat}&lon=${point.lon}&osm_tag=highway"

        val responseListener = object : ResponseListener {
            override fun onResponse(response: JSONObject) {
                streetlistener.onStreetFound(extractLocation(response))
            }
        }
        requestHandler.doRequest(url, responseListener)
    }

}






