package com.se7en.market.Response.Cart

import com.se7en.market.Model.CartModel

data class CreateOrAddToCart(
    val status: String,
    val system_message: String,
    val `data`: CartModel,
    val messages: List<String> = listOf(),
    val errors: List<String> = listOf(),
)