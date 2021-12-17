package com.example.sanic.location.geofence;


import com.example.sanic.Point;

public interface NextNearLocation {

    //sets next geofence point
    void setNextNearLocation(Point point, Double radiusInMeters);

}
