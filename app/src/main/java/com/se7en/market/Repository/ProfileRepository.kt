package com.se7en.market.Repository

import androidx.lifecycle.MutableLiveData
import com.se7en.market.Model.UserModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Network.RetrofitService
import com.se7en.market.Response.User.Profile
import com.se7en.market.Response.User.UpdateProfile
import com.se7en.market.Response.Wallet.WalletPayemetResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepository {

    companion object {
        @Volatile
        private var INSTANCE: ProfileRepository? = null

        fun getInstance(): ProfileRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: ProfileRepository().apply { INSTANCE = this }
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
                    if (response.isSuccessful && response.body()?.status == "success") {
                        _profileStatus.value = NetworkResult.Success(response.body()!!)
                    } else {
                        _profileStatus.value =
                            NetworkResult.Error("Error: ${response.body()?.errors?.get(0)}")
                    }
                }

                override fun onFailure(call: Call<Profile>, t: Throwable) {
                    _profileStatus.value = NetworkResult.Error("Error: ${t.message}")
                }
            })
    }

    fun updateProfile(token: String, user: UserModel) {
        RetrofitService.apiService().updateProfile("Bearer $token", user)
            .enqueue(object : Callback<UpdateProfile> {
                override fun onResponse(
                    call: Call<UpdateProfile>,
                    response: Response<UpdateProfile>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success") {
                        _updateStatus.value = NetworkResult.Success(response.body()!!)
                    } else {
                        _updateStatus.value =
                            NetworkResult.Error("Error: ${response.body()?.errors?.get(0)}")
                    }
                }

                override fun onFailure(call: Call<UpdateProfile>, t: Throwable) {
                    _updateStatus.value = NetworkResult.Error("Error: ${t.message}")
                }
            })
    }

    fun incrementWallet(token: String, mapData: Map<String, Long>): MutableLiveData<NetworkResult<WalletPayemetResponse>> {
        val paymentStatus = MutableLiveData<NetworkResult<WalletPayemetResponse>>()
        paymentStatus.value = NetworkResult.Loading
        RetrofitService.apiService().incrementWallet(token, mapData)
            .enqueue(object : Callback<WalletPayemetResponse> {
                override fun onResponse(
                    call: Call<WalletPayemetResponse>,
                    response: Response<WalletPayemetResponse>
                ) {
                    if (response.isSuccessful) {
                        paymentStatus.value = NetworkResult.Success(response.body()!!)
                    } else {
                        paymentStatus.value = NetworkResult.Error("Failed to update wallet.")
                    }
                }

                override fun onFailure(call: Call<WalletPayemetResponse>, t: Throwable) {
                    paymentStatus.value = NetworkResult.Error("Error: ${t.message}")
                }
            })
        return paymentStatus
    }
}
