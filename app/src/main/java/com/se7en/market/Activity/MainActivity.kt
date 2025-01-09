package com.se7en.market.Activity

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.se7en.market.Network.NetworkResult
import com.se7en.market.R
import com.se7en.market.Utils.Helper
import com.se7en.market.ViewModel.CartViewModel
import com.se7en.market.databinding.ActivityMainBinding


class MainActivity : BaseActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navView: BottomNavigationView
    private val cartViewModel: CartViewModel by viewModels<CartViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        NavigationUI.setupWithNavController(navView, navController)

        setCartBadge()

        val helper = Helper(this)
        Log.i("TAG", "appSignatures: ${helper.appSignatures}")
    }

    private fun setCartBadge() {

        cartViewModel.cartsStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> {}
                is NetworkResult.Success -> {
                    val cartCount = result.response.data.count()
                    val badge = navView.getOrCreateBadge(R.id.navigation_cart)
                    if (cartCount > 0) {
                        badge.isVisible = true
                        badge.backgroundColor = getColor(R.color.green)
                        badge.badgeTextColor = getColor(R.color.white)
                        badge.number = cartCount
                    } else
                        badge.isVisible = false
                }

                is NetworkResult.Error -> {}
            }
        }

        cartViewModel.getAllCarts()

    }
}