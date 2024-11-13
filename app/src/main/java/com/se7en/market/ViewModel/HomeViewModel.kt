package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.se7en.market.Repository.PreferencesRepository

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    val preferencesRepository = PreferencesRepository.getInstance(application)
    fun loadActiveAddress() = preferencesRepository.loadActiveAddress()

}