package com.example.sanic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.sanic.map.GameActivity
import android.view.View
import androidx.fragment.app.add
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import kotlin.math.log


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val requestHandler = VolleyRequestHandler(this)
//        val photonApiManager = PhotonApiManager(requestHandler)
//
//
//        val point = Point(51.414180,5.514767,"0")
//
//        photonApiManager.getClosestStreet(point) {
//            Log.i("LocationFound", "onCreate: original: $point corrected: $it")
//        }


    }


    fun startGame(view: View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

    fun openSettingMain(view: View) {
        Log.d("MainActivity", "openSettingMain: ")
        val findFragmentByTag = supportFragmentManager.findFragmentByTag("Settings")
        if (findFragmentByTag != null) {
            onBackPressed()
            return
        }
        supportFragmentManager.commit {
            replace<SettingsFragment>(R.id.fragmentContainerView, "Settings")
            setReorderingAllowed(true)
            addToBackStack("null")
        }
    }

}