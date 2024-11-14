package com.se7en.market.Model

import java.util.Date

data class UserModel(
    val id: Int = 0,
    var first_name: String? = null,
    var last_name: String? = null,
    val phone: String = "",
    val name: String = "",
    var email: String = "",
    val email_verified_at: String = "",
    var gender: String = "",
    var date_of_birth: String? = null,
    val wallet_balance: String = "",
    val otp_request_count: Int = 0,
    val otp_ban_until: String = "",
    val created_at: String = "",
    val updated_at: String = "",
    val theme: String = "",
    val theme_color: String = "",
    val sort_order: Int = 0,
    val role: String = "",
    val has_address: Boolean = false,
    val roles: List<String>? = null
)
