package com.se7en.market.Response.Cart

import com.se7en.market.Model.CartModel

data class GetCartDetails(
    val status: String,
    val system_message: String,
    val `data`: CartModel,
    val messages: List<String>,
    val errors: List<String>,
)