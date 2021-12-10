package com.example.sanic.api

import android.content.Context
import android.util.Log
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class VolleyRequestHandler(private val context: Context) : RequestHandler {

    private val queue = Volley.newRequestQueue(context)

    override fun doRequest(url: String, responeListener: ResponseListener) {
        val jsonObjectRequest = JsonObjectRequest(
            url, {
                   response -> responeListener.onResponse(response)
            },
            {
                Log.d("henk", "doRequest: ")
            })
        queue.add(jsonObjectRequest)
    }

}