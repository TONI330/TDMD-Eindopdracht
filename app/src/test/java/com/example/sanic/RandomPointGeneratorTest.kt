package com.example.sanic

import com.example.sanic.map.RandomPointGenerator
import org.junit.Test
import kotlin.math.cos

class RandomPointGeneratorTest {

    @Test
    fun latInRange() {
        //arrange
        val testRandomPoint = RandomPointGenerator(Point(50.0, 4.0, "test"))
        var testsFailed = 0

        //act
        for (i in 1..100) {
            testRandomPoint.getRandomPoint(50.0).apply {
                println("Testpoint $i: $this")
                testsFailed += latValid(this, 50.0)
            }
        }

        //assert
        println("Tests failed for lat: $testsFailed")
        assert(testsFailed == 0)

    }

    private fun latValid(actualPoint: Point, d: Double): Int {
        if(actualPoint.lat > actualPoint.lat + d / 111320 || actualPoint.lat < actualPoint.lat - d * 111320) {
            return 1
        }
        return 0
    }

    @Test
    fun lonInRange() {
        //arrange
        val testRandomPoint = RandomPointGenerator(Point(50.0, 4.0, "test"))
        var testsFailed = 0

        //act
        for (i in 1..100) {
//            testRandomPoint.getRandomPoint(50.0, PointListener { point ->
//                println("Testpoint $i: $point")
//                testsFailed += lonValid(point, 50.0)
//            })

            testRandomPoint.getRandomPoint(50.0).apply{
                println("Testpoint $i: $this")
                testsFailed += lonValid(this, 50.0)
            }

        }

        //assert
        println("Tests failed for lon: $testsFailed")
        assert(testsFailed == 0)

    }

    private fun lonValid(actualPoint: Point, d: Double): Int {
        if(actualPoint.lon > actualPoint.lon + d / (40075000 * (cos(actualPoint.lat) / 360)) ||
            actualPoint.lat < actualPoint.lat - d * (40075000 * (cos(actualPoint.lat) / 360))) {
            return 1
        }
        return 0
    }

}