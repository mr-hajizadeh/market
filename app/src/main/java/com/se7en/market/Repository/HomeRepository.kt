package com.se7en.market.Repository

import com.se7en.market.Network.RetrofitService

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

    fun getAllData(token: String, latLon: Map<String, Double>) =
        RetrofitService.apiService().getAllData(token, latLon)
}