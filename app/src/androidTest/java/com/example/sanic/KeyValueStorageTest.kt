package com.example.sanic

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.sanic.KeyValueStorage.setValue
import com.example.sanic.KeyValueStorage.getValue
import androidx.test.rule.ActivityTestRule
import com.example.sanic.MainActivity
import com.example.sanic.KeyValueStorage
import com.example.sanic.R
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
@RunWith(AndroidJUnit4::class)
class KeyValueStorageTest {

    @get:Rule
    var activityTestRule = ActivityTestRule(
        MainActivity::class.java
    )

    @Test
    fun setNewValue() {
        //arrange
        val testValue = "testValue"

        //act
        setValue(activityTestRule.activity, "value", testValue)
        val actualValue = getValue(activityTestRule.activity, "value")

        //assert
        Assert.assertEquals(testValue, actualValue)
    }

    @Test
    fun setNewValueWithID() {
        //arrange
        val testKeyID = R.string.testKey
        val expectedValue = "testedkey"

        //act
        setValue(activityTestRule.activity, testKeyID, "testedkey")
        val actualValue = getValue(activityTestRule.activity, testKeyID)

        //assert
        Assert.assertEquals(expectedValue, actualValue)
    }

    @Test
    fun checkNonExistingKey() {
        //arrange
        val assertValue = ""

        //act
        val actualValue = getValue(activityTestRule.activity, "yabadabadoo")

        //assert
        Assert.assertTrue(actualValue!!.isEmpty())
    }

    @Test
    fun checkDuplicateInput() {
        //arrange
        val oldValue = "val"
        val newValue = "val"

        //act
        setValue(activityTestRule.activity, "keyValue", oldValue)
        Assert.assertFalse(setValue(activityTestRule.activity, "keyValue", newValue))
    }
}