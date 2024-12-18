package com.se7en.market.Response.Cart

data class DeleteCart(
    val status: String,
    val system_message: String,
    val `data`: List<Any>,
    val messages: List<String>,
    val errors: List<String>
)