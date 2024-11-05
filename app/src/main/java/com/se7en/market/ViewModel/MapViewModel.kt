package com.se7en.market.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.MapRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapViewModel : ViewModel() {

    private var mapRepository: MapRepository = MapRepository()
    private val _geocodingStatus = MutableLiveData<NetworkResult<ReverseGeocoding>>()
    val geocodingStatus get() = _geocodingStatus

    fun reverseGeocoding(lat: Double, lon: Double) {
        viewModelScope.launch {
            _geocodingStatus.value = NetworkResult.Loading
            mapRepository.reverseGeocoding(lat, lon)
                .enqueue(object : Callback<ReverseGeocoding> {
                    override fun onResponse(
                        call: Call<ReverseGeocoding>,
                        response: Response<ReverseGeocoding>
                    ) {
                        if (response.isSuccessful)
                            _geocodingStatus.value = NetworkResult.Success(response.body()!!)
                    }

                    override fun onFailure(call: Call<ReverseGeocoding>, t: Throwable) {
                        _geocodingStatus.value = NetworkResult.Error("Error")
                    }

                })
        }
    }

}