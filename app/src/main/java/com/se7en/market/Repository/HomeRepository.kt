package com.se7en.market.Repository

import com.se7en.market.Network.RetrofitService
import com.se7en.market.Response.SliderResponse
import retrofit2.Call

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

    fun getSliderList() = RetrofitService.apiService().getSliderList()

}