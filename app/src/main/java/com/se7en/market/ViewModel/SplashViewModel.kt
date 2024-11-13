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
    private val profileRepository = ProfileRepository()

    private var _profileStatus = MutableLiveData<NetworkResult<Profile>>()
    val profileStatus get() = _profileStatus

    fun getLoginInfo(): LiveData<LoginInfoModel> {
        return preferencesRepository.getLoginInfo()
    }

    fun getProfile(token: String) {
        viewModelScope.launch {
            _profileStatus.value = NetworkResult.Loading
            profileRepository.getProfile("Bearer $token").enqueue(object : Callback<Profile> {
                override fun onResponse(call: Call<Profile>, response: Response<Profile>) {
                    if (response.isSuccessful && response.body()?.status == "success")
                        _profileStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _profileStatus.value =
                            NetworkResult.Error(response.body()?.errors?.get(0).toString())
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    _profileStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
        }
    }

}