package com.se7en.market.Repository

import androidx.lifecycle.MutableLiveData
import com.se7en.market.Model.UserModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Network.RetrofitService
import com.se7en.market.Response.User.Profile
import com.se7en.market.Response.User.UpdateProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository {

    companion object {
        @Volatile
        private var INSTANCE: ProfileRepository? = null
        fun getInstance(): ProfileRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfileRepository().also { INSTANCE = it }
            }
        }
    }

    private var _profileStatus = MutableLiveData<NetworkResult<Profile>>()
    val profileStatus get() = _profileStatus

    private var _updateStatus = MutableLiveData<NetworkResult<UpdateProfile>>()
    val updateStatus get() = _updateStatus

    fun getProfile(token: String) {
        _profileStatus.value = NetworkResult.Loading
        RetrofitService.apiService().getProfile("Bearer $token")
            .enqueue(object : Callback<Profile> {
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

    fun updateProfile(token: String, user: UserModel) {
        _updateStatus.value = NetworkResult.Loading
        RetrofitService.apiService().updateProfile("Bearer $token", user)
            .enqueue(object : Callback<UpdateProfile> {
                override fun onResponse(call: Call<UpdateProfile>, response: Response<UpdateProfile>) {
                    if (response.isSuccessful && response.body()?.status == "success")
                        _updateStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _updateStatus.value =
                            NetworkResult.Error(response.body()?.errors?.get(0).toString())
                }

                override fun onFailure(call: Call<UpdateProfile>, t: Throwable) {
                    _updateStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

}