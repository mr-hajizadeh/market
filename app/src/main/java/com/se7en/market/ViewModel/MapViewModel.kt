package com.se7en.market.ViewModel

import androidx.lifecycle.ViewModel
import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.Network.RetrofitService
import com.se7en.market.Repository.MapRepository
import retrofit2.Call

class MapViewModel : ViewModel() {

    private var mapRepository: MapRepository = MapRepository()

    fun reverseGeocoding(lat: Double, lon: Double): Call<ReverseGeocoding> {
        return mapRepository.reverseGeocoding(lat, lon)
    }

}