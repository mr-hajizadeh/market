package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.se7en.market.Model.LoginInfoModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Repository.ProfileRepository
import com.se7en.market.Response.User.Profile
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val profileRepository = ProfileRepository.getInstance()
    val profileStatus get() = profileRepository.profileStatus

    fun getLoginInfo(): LiveData<LoginInfoModel> {
        return preferencesRepository.getLoginInfo()
    }

    fun getProfile() {
        profileRepository.getProfile(preferencesRepository.getToken())
    }

}