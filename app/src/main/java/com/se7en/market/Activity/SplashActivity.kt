package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.se7en.market.ViewModel.SplashViewModel
import com.se7en.market.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewmodel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewmodel = ViewModelProvider(this)[SplashViewModel::class.java]

        checkNetworkStatus()

    }

    private fun checkNetworkStatus() {
        loadingProgress()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        val connectivityManager =
            getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private fun checkPhone() {

        viewmodel.getPhoneNumber().observe(this) { phone ->
            if (phone.isNullOrEmpty())
                Handler(Looper.getMainLooper()).postDelayed({
                    showLoginActivity()
                }, 3000)
            else
                Toast.makeText(this@SplashActivity, "login", Toast.LENGTH_LONG).show()
        }

    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            runOnUiThread { checkPhone() }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            loadingProgress(false)
        }
    }

    private fun showLoginActivity() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
    }

    private fun loadingProgress(isLoading: Boolean = true) {
        val progress = binding.loading
        val errorMessage = binding.errorMessageTxt
        if (isLoading) {
            progress.visibility = View.VISIBLE
            errorMessage.visibility = View.GONE
        } else {
            progress.visibility = View.GONE
            errorMessage.visibility = View.VISIBLE
        }
    }

}