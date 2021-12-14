package com.example.sanic.map

import android.util.Log
import com.example.sanic.Point
import kotlin.math.log


class GameManager(
    private val gameActivity: GameActivity,
    private val randomPointGenerator: RandomPointGenerator
) {

    val checkPoints: ArrayList<Point> = ArrayList()

    private fun startGpsUpdates() {
        //TODO get locationmanager to start gps updates
    }

    private fun setNextCheckPoint() {
        //randomPoint.getRandomPoint(50.0)
    }

    fun checkValid(point: Point): Boolean {
        for (checkPoint in checkPoints) {
            if (point.id.equals(checkPoint.id))
                return false
        }
        return true
    }

    fun tooMuchTries() {
        Log.d("GameManager", "tooMuchTries: ")
    }

    var tries: Int = 0
    fun generateRandom() {
        randomPointGenerator.getRandomSnappedPoint(500.0) { point ->
            if (tries > 10) {
                tries = 0
                tooMuchTries()
            } else {
                if (checkValid(point)) {
                    tries = 0
                    checkPoints.add(point)
                    gameActivity.drawPointOnMap(point)
                } else {
                    tries++
                    generateRandom()
                }
            }
        }
    }


}