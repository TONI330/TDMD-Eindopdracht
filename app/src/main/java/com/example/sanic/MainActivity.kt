package com.example.sanic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import com.example.sanic.api.VolleyRequestHandler
import com.example.sanic.api.PhotonApiManager
import com.example.sanic.api.Streetlistener

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val requestHandler = VolleyRequestHandler(this)
//        val photonApiManager = PhotonApiManager(requestHandler)
//        val point = Point(51.594,4.764,0)
//
//        photonApiManager.getClosestStreet(point) {
//            Log.i("MainActivity", "onCreate: $point")
//        }


    }



    fun startGame(view: android.view.View) {
        val intent = Intent(this, GameActivity::class.java)
        startActivity(intent)
    }

}