package com.se7en.market.Model

data class UserModel(
    val id: Int,
    val first_name: Any,
    val last_name: Any,
    val phone: String,
    val name: Any,
    val email: Any,
    val email_verified_at: Any,
    val gender: Any,
    val date_of_birth: Any,
    val wallet_balance: String,
    val otp_request_count: Int,
    val otp_ban_until: Any,
    val created_at: String,
    val updated_at: String,
    val theme: String,
    val theme_color: Any,
    val sort_order: Int,
    val role: Any,
    val has_address: Boolean,
    val roles: List<Any>
)
