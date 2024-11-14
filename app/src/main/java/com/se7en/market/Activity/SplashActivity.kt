package com.se7en.market.Activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicator
import com.se7en.market.Network.NetworkResult
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
        responseListener()
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

        viewmodel.getLoginInfo().observe(this) { loginInfo ->
            if (loginInfo.phone.isEmpty() && loginInfo.token.isEmpty())
                Handler(Looper.getMainLooper()).postDelayed(
                    { customIntent(LoginActivity::class.java) }, 3000
                )
            else
                viewmodel.getProfile()
        }

    }

    private fun responseListener() {
        viewmodel.profileStatus.observe(this) { result ->
            when (result) {
                is NetworkResult.Loading -> loadingProgress()

                is NetworkResult.Success -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (result.response.data.has_address)
                            customIntent(MainActivity::class.java)
                        else
                            customIntent(MapActivity::class.java)
                    }, 1000)

                }

                is NetworkResult.Error -> {
                    loadingProgress(false)
                    Log.i("TAG", "responseListener: ${result.message}")
                }
            }
        }
    }

    private fun customIntent(activityClass: Class<out Activity>) {
        startActivity(Intent(this@SplashActivity, activityClass))
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