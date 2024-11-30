package com.se7en.market.Repository

import androidx.lifecycle.MutableLiveData
import com.se7en.market.Model.SliderModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Network.RetrofitService
import com.se7en.market.Response.Home.HomeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeRepository {

    companion object {
        @Volatile
        var INSTANCE: HomeRepository? = null
        fun getInstance(): HomeRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: HomeRepository().also { INSTANCE = it }
            }
        }
    }

    private var _sliderStatus = MutableLiveData<NetworkResult<List<SliderModel>>>()
    val sliderStatus get() = _sliderStatus

    private var _homeStatus = MutableLiveData<NetworkResult<HomeResponse>>()
    val homeStatus get() = _homeStatus

    fun getSliderList() {
        _sliderStatus.value = NetworkResult.Loading
        RetrofitService.apiService().getSliderList().enqueue(object : Callback<List<SliderModel>> {
            override fun onResponse(
                call: Call<List<SliderModel>>,
                response: Response<List<SliderModel>>,
            ) {
                if (response.isSuccessful && response.body()?.isNotEmpty() == true)
                    _sliderStatus.value = NetworkResult.Success(response.body()!!)
                else
                    _sliderStatus.value = NetworkResult.Error("Error")

            }

            override fun onFailure(call: Call<List<SliderModel>>, t: Throwable) {
                _sliderStatus.value = NetworkResult.Error("Error ${t.message}")
            }
        })
    }

    fun getAllData(
        token: String,
        latLon: Map<String, Double>,
    ) {
        _homeStatus.value = NetworkResult.Loading
        RetrofitService.apiService().getAllData(token, latLon).enqueue(object : Callback<HomeResponse> {
            override fun onResponse(
                call: Call<HomeResponse>,
                response: Response<HomeResponse>,
            ) {
                if (response.isSuccessful && response.body() != null) {
                    _homeStatus.value = NetworkResult.Success(response.body()!!)
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                _homeStatus.value = NetworkResult.Error("Error ${t.message}")
            }
        })
    }

}