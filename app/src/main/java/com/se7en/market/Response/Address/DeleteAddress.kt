package com.se7en.market.Response.Address

data class DeleteAddress(
    val status: String,
    val system_message: String,
    val `data`: Any,
    val messages: List<String>,
    val errors: List<Any>
)