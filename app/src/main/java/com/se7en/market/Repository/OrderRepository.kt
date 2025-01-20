package com.se7en.market.Repository

import com.se7en.market.Network.RetrofitService

class OrderRepository {

    fun getAllOrders(token: String) =
        RetrofitService.apiService().getAllOrders(token)

    fun getDistanceMatrix(type: String, origins: String, distance: String) =
        RetrofitService.apiService("https://api.neshan.org")
            .getDistanceMatrix(type, origins, distance)

    fun getOrderDetails(token: String, orderId: Int) =
        RetrofitService.apiService().getOrderDetails(token, orderId)

}