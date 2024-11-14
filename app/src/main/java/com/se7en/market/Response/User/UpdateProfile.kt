package com.se7en.market.Response.User

data class UpdateProfile(
    val status: String,
    val system_message: String,
    val `data`: Boolean,
    val messages: List<String>,
    val errors: List<Any>
)