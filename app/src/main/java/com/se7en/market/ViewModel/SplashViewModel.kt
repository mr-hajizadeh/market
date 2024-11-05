package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.se7en.market.Model.LoginInfo
import com.se7en.market.Repository.PreferencesRepository

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository(application)

    fun getPhoneNumber(): LiveData<LoginInfo> {
        return preferencesRepository.getLoginInfo()
    }

}