package com.example.sanic

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.sanic", appContext.packageName)
    }

    @Test
    fun nameTest()
    {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val stringId: Int = appContext.applicationInfo.labelRes
        assertEquals(R.string.app_name,stringId)

    }
}