package com.se7en.market.Activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.se7en.market.R
import com.se7en.market.databinding.ActivityAddBinding
import org.neshan.common.model.LatLng
import org.neshan.mapsdk.MapView
import org.neshan.mapsdk.internal.elements.MapElement

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private lateinit var map: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize MapView
        map = binding.map

        // Set initial map view to a specific location (e.g., Mashhad)
        map.moveCamera(LatLng(36.2972, 59.6067), 0f)
        map.setZoom(14f, 0f)
    }
}