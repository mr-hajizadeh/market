package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.se7en.market.Repository.HomeRepository
import com.se7en.market.Repository.PreferencesRepository
import java.nio.DoubleBuffer

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val homeRepository = HomeRepository.getInstance()
    private val token = preferencesRepository.getToken()

    val sliderStatus get() = homeRepository.sliderStatus
    val homeStatus get() = homeRepository.homeStatus

    fun loadActiveAddress() = preferencesRepository.loadActiveAddress()
    fun getSliderList() = homeRepository.getSliderList("Bearer $token")
    fun getAllData(lat: Double, lon: Double) = homeRepository.getAllData(
        "Bearer $token",
        mapOf("latitude" to lat, "longitude" to lon)
    )

}