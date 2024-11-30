package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.se7en.market.Model.SliderModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.HomeRepository
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Response.Home.HomeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val homeRepository = HomeRepository.getInstance()
    private val token = preferencesRepository.getToken()

    private var _sliderStatus = MutableLiveData<NetworkResult<List<SliderModel>>>()
    val sliderStatus get() = _sliderStatus

    private var _homeStatus = MutableLiveData<NetworkResult<HomeResponse>>()
    val homeStatus get() = _homeStatus

    fun loadActiveAddress() = preferencesRepository.loadActiveAddress()

    fun getSliderList() {
        _sliderStatus.value = NetworkResult.Loading
        homeRepository.getSliderList().enqueue(object : Callback<List<SliderModel>> {
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
        latLon: Map<String, Double> = mapOf(
            "latitude" to 36.320519437955,
            "longitude" to 59.667323402147
        ),
    ) {
        homeStatus.value = NetworkResult.Loading
        homeRepository.getAllData("Bearer $token", latLon).enqueue(object : Callback<HomeResponse> {
            override fun onResponse(
                call: Call<HomeResponse>,
                response: Response<HomeResponse>,
            ) {
                if (response.isSuccessful && response.body() != null) {
                    homeStatus.value = NetworkResult.Success(response.body()!!)
                }
            }

            override fun onFailure(call: Call<HomeResponse>, t: Throwable) {
                homeStatus.value = NetworkResult.Error("Error ${t.message}")
            }
        })
    }

}