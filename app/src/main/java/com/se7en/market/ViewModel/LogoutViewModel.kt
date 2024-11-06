package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.se7en.market.Repository.PreferencesRepository

class LogoutViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = PreferencesRepository(application)

    fun logout() { repository.logout() }

}