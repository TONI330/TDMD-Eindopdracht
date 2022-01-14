package com.example.sanic.api

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class VolleyRequestHandler(context: Context) : RequestHandler {

    private val queue = Volley.newRequestQueue(context)

    override fun getRequest(url: String, responeListener: ResponseListener) {
        val jsonObjectRequest = JsonObjectRequest(
            url, { response ->
                responeListener.onResponse(response)
            },
            {
                Log.d("henk", "doRequest: ")
            })
        queue.add(jsonObjectRequest)
    }

    override fun postRequest(url: String, jsonObject: JSONObject, responeListener: ResponseListener) {
        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.POST,
            url,
            jsonObject,
            { response ->
                responeListener.onResponse(response)
            },
            {
                Log.d("henk", "doRequest: ")
            })
        queue.add(jsonObjectRequest)
    }

    override fun stop() {
        queue.stop()
    }

}