package com.example.sanic

import android.content.Context
import androidx.annotation.StringRes
import androidx.preference.PreferenceManager

object KeyValueStorage {
    fun setValue(context: Context, @StringRes stringID: Int, value: String): Boolean {
        return setValue(context, context.getString(stringID), value)
    }

    fun setValue(context: Context, key: String?, newValue: String): Boolean {
        val oldValue = getValue(context, key)
        if (oldValue == newValue) return false
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPref.edit()
        editor.putString(key, newValue)
        editor.apply()
        return true
    }

    fun getValue(context: Context, key: String?): String? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        var value = sharedPref.getString(key, null)
        if (value == null)
        {
            value = sharedPref.getInt(key, -1).toString()
        }
        return value
    }

    fun getValue(context: Context, @StringRes stringID: Int): String? {
        return getValue(context, context.getString(stringID))
    }

    fun getBoolean(context: Context, key: String?) : Boolean? {
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
        return sharedPref.getBoolean(key, true)
    }
}