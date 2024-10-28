package com.se7en.market.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.se7en.market.Fragment.MapFragment
import com.se7en.market.R
import com.se7en.market.databinding.ActivityMapBinding

class MapActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMapBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val fragment = MapFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.containerActivityMap, fragment)
            .commit()

    }
}