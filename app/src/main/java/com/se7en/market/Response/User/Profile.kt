package com.se7en.market.Response.User

import com.se7en.market.Model.UserModel

data class Profile(
    val status: String,
    val system_message: String,
    val `data`: UserModel,
    val messages: List<Any>,
    val errors: List<Any>
)