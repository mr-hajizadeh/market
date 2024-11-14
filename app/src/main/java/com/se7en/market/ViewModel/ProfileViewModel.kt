package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.se7en.market.Model.UserModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Repository.ProfileRepository
import com.se7en.market.Response.User.Profile
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val profileRepository = ProfileRepository.getInstance()
    private val token = preferencesRepository.getToken()
    val profileStatus get() = profileRepository.profileStatus
    val updateStatus get() = profileRepository.updateStatus

    fun getProfile() {
        profileRepository.getProfile(token)
    }

    fun updateProfile(user: UserModel) {
        profileRepository.updateProfile(token, user)
    }

}