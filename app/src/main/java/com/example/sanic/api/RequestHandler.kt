package com.example.sanic.api

import org.json.JSONObject

interface RequestHandler {
    fun getRequest(url: String, responeListener: ResponseListener)
    fun postRequest(url: String,jsonObject: JSONObject, responeListener: ResponseListener)
    fun stop()
}