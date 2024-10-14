package com.se7en.market.Repository

import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.Network.RetrofitService
import retrofit2.Call

class MapRepository() {

    fun reverseGeocoding(lat: Double, lon: Double): Call<ReverseGeocoding> {
        return RetrofitService.apiService().reverseGeocoding(lat, lon)
    }

}