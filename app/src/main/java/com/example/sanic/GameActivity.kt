package com.example.sanic

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import com.example.sanic.databinding.ActivityGameBinding
import com.google.android.gms.location.FusedLocationProviderClient
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class GameActivity : AppCompatActivity() {

    private var fusedLocationClient: FusedLocationProviderClient? = null
    lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(LayoutInflater.from(this))
        setContentView(R.layout.activity_game)

        Configuration.getInstance().load(this, getSharedPreferences("osm_config", Context.MODE_PRIVATE))

        setupMaps()
    }

    private fun setupMaps() {

        binding.map.apply{
            setTileSource(TileSourceFactory.MAPNIK)

            val locationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this@GameActivity), this)
            locationOverlay.enableMyLocation()
            overlays.add(locationOverlay)

            controller.apply {
                
                setZoom(6.0)
            }
        }
    }

}