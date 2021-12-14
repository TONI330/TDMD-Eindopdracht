package com.example.sanic

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sanic.map.GameActivity
import android.util.Log
import android.view.View

import com.example.sanic.api.VolleyRequestHandler
import com.example.sanic.api.PhotonApiManager

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

}