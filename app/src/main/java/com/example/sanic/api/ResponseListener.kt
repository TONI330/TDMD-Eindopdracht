package com.example.sanic.api

import org.json.JSONObject

interface ResponseListener {
    fun onResponse(response: JSONObject)
}