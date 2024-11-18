package com.se7en.market.Response.OTP

import com.se7en.market.Model.UserModel

data class Confirm(
    val status: String,
    val system_message: String,
    val `data`: Data,
    val messages: List<String>,
    val errors: List<String>
) {
    data class Data(
        val token: String,
        val user: UserModel
    )
}