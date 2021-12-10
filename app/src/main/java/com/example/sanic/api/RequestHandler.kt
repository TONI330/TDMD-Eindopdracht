package com.example.sanic.api

interface RequestHandler {
    fun doRequest(url: String, responeListener: ResponseListener)
}