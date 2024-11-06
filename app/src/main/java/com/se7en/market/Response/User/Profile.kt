package com.se7en.market.Response.User

import com.se7en.market.Model.User

data class Profile(
    val status: String,
    val system_message: String,
    val `data`: User,
    val messages: List<Any>,
    val errors: List<Any>
)