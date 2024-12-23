package com.se7en.market.Repository

import com.se7en.market.Network.RetrofitService

class OrderRepository {

    fun getDistanceMatrix(type: String, origins: String, distance: String) =
        RetrofitService.apiService("https://api.neshan.org")
            .getDistanceMatrix(type, origins, distance)

}