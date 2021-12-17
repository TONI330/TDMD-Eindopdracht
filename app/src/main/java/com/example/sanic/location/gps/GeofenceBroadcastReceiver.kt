package com.example.sanic.location.gps

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.GeofencingEvent

open class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(LOGTAG, "Received a trigger with intent: $intent")
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            Log.d(LOGTAG, "Geofence error:" + geofencingEvent.errorCode)
        }
        for (geofence in geofencingEvent.triggeringGeofences) {
            Log.d(LOGTAG, "Geofence entered: " + geofence.requestId)
//            if (Location.geofenceBroadcastReceiver != null) Location.geofenceBroadcastReceiver.onReceive(
//                context,
//                intent
//            )
        }
    }

    companion object {
        var LOGTAG = GeofenceBroadcastReceiver::class.java.name
    }
}