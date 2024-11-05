package com.se7en.market.Response.OTP

data class Code(
    val status: String,
    val system_message: String,
    val `data`: Data,
    val messages: List<Any>,
    val errors: List<Any>
) {
    data class Data(
        val code: String
    )
}