package com.se7en.market.Activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.se7en.market.Fragment.AddressListFragment
import com.se7en.market.R
import com.se7en.market.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddressBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        loadFragment()
    }

    private fun loadFragment() {

        val fragment = AddressListFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun initViews() {
        binding.backBtn.setOnClickListener {
            finish()
        }
    }

}