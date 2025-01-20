package com.se7en.market.Response.Order

import com.se7en.market.Model.OrderModel

data class GetAllOrders(
    val status: String,
    val system_message: String,
    val `data`: MutableList<OrderModel>,
    val messages: List<String>,
    val errors: List<String>
)