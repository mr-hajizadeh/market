package com.se7en.market.Response.OTP

import com.se7en.market.Model.User

data class Confirm(
    val status: String,
    val system_message: String,
    val `data`: Data,
    val messages: List<Any>,
    val errors: List<Any>
) {
    data class Data(
        val token: String,
        val user: User
    )
}