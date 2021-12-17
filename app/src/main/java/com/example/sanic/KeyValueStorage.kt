package com.example.sanic

import android.app.Activity
import android.content.Context
import androidx.annotation.StringRes

object KeyValueStorage {
    fun setValue(activity: Activity, @StringRes stringID: Int, value: String): Boolean {
        return setValue(activity, activity.getString(stringID), value)
    }

    fun setValue(activity: Activity, key: String?, newValue: String): Boolean {
        val oldValue = getValue(activity, key)
        if (oldValue == newValue) return false
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(key, newValue)
        editor.apply()
        return true
    }

    fun getValue(activity: Activity, key: String?): String? {
        val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getString(key, "")
    }

    fun getValue(activity: Activity, @StringRes stringID: Int): String? {
        return getValue(activity, activity.getString(stringID))
    }
}