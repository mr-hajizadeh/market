package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.se7en.market.Repository.HomeRepository
import com.se7en.market.Repository.PreferencesRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val homeRepository = HomeRepository.getInstance()
    private val token = preferencesRepository.getToken()

    val sliderStatus get() = homeRepository.sliderStatus
    val homeStatus get() = homeRepository.homeStatus

    fun loadActiveAddress() = preferencesRepository.loadActiveAddress()
    fun getSliderList() = homeRepository.getSliderList()
    fun getAllData() = homeRepository.getAllData(
        "Bearer $token",
        mapOf("latitude" to 36.320519437955, "longitude" to 59.667323402147)
    )

}