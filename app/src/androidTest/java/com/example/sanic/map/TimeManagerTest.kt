package com.example.sanic.map

import android.content.Context
import android.util.Log
import androidx.activity.viewModels
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.sanic.MainActivity
import com.example.sanic.ScoreViewModel
import org.junit.Assert.*

import junit.framework.TestCase

import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TimeManagerTest {


    @get:Rule
    var activityTestRule = ActivityTestRule(
        MainActivity::class.java
    )


    @Test
    fun setTimer() {
        var testBool = false
        var timeManager: TimeManager? = null
        activityTestRule.runOnUiThread{
            timeManager = TimeManager(activityTestRule.activity)
        }
        val test = Thread {
            timeManager!!.setTimer(10) {
                testBool = true
            }
            Thread.sleep(5)
            Thread.sleep(100)
        }
        test.start()
        test.join()
        assertTrue(testBool)
    }

    fun test(): Unit {
        Log.d("henk", "test: ")
        println("test")
    }



    @Test
    fun cancelTimer() {
        var testBool = false
        var timeManager: TimeManager? = null
        activityTestRule.runOnUiThread{
            timeManager = TimeManager(activityTestRule.activity)
        }
        val test = Thread {
            timeManager!!.setTimer(10) {
                testBool = true
            }
            Thread.sleep(5)
            timeManager!!.cancelTimer()
            Thread.sleep(100)
        }
        test.start()
        test.join()
        assertFalse(testBool)
    }


}