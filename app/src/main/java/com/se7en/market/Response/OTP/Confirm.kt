package com.se7en.market.Response.OTP

import com.se7en.market.Model.UserModel

data class Confirm(
    val status: String,
    val system_message: String,
    val `data`: Data,
    val messages: List<Any>,
    val errors: List<Any>
) {
    data class Data(
        val token: String,
        val userModel: UserModel
    )
}