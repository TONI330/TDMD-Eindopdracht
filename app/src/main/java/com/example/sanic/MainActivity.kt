package com.example.sanic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sanic.map.GameActivity
import android.view.View
import android.widget.TextView
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    private var instructionsVisible: Boolean = true
    private lateinit var instructionsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //updateSettings()
    }


    fun startGame(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    fun openSettingMain(view: View) {
        Log.d("MainActivity", "openSettingMain: ")
        val findFragmentByTag = supportFragmentManager.findFragmentByTag("Settings")
        if (findFragmentByTag != null) {
            //updateSettings()
            onBackPressed()
            return
        }
        supportFragmentManager.commit {
            replace<SettingsFragment>(R.id.fragmentContainerView, "Settings")
            //updateSettings()
            setReorderingAllowed(true)
            addToBackStack("null")
        }
    }



}