package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.se7en.market.ViewModel.SplashViewModel
import com.se7en.market.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var viewmodel: SplashViewModel
    private lateinit var connectivityManager: ConnectivityManager

    private var progress: CircularProgressIndicator? = null
    private var errorMessage: TextView? = null
    private var tryBtn: MaterialButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewmodel = ViewModelProvider(this)[SplashViewModel::class.java]
        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        initViews()
        tryToConnect()
        checkInternetConnection()
    }

    private fun tryToConnect() {
        tryBtn?.setOnClickListener {
            checkInternetConnection()
        }
    }

    private fun checkInternetConnection() {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        val isConnected =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

        if (isConnected) {
            loadingProgress()
            checkPhone()
        } else
            loadingProgress(false)
    }

    private fun initViews() {

        progress = binding.loading
        errorMessage = binding.errorMessageTxt
        tryBtn = binding.tryBtn

    }

    private fun checkPhone() {

        viewmodel.getPhoneNumber().observe(this) { loginInfo ->
            if (loginInfo.phone.isEmpty())
                Handler(Looper.getMainLooper()).postDelayed({
                    showLoginActivity()
                }, 3000)
            else
                Toast.makeText(this@SplashActivity, "login", Toast.LENGTH_LONG).show()
        }

    }

    private fun showLoginActivity() {
        startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        finish()
    }

    private fun loadingProgress(isLoading: Boolean = true) {
        if (isLoading) {
            progress?.visibility = View.VISIBLE
            tryBtn?.visibility = View.GONE
            errorMessage?.visibility = View.GONE
        } else {
            progress?.visibility = View.GONE
            errorMessage?.visibility = View.VISIBLE
            tryBtn?.visibility = View.VISIBLE
        }
    }

}