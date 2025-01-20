package com.se7en.market.Model

data class PaymentModel(
    val receiver_name: String = "",
    val receiver_phone: String = "",
    val useWallet: Byte = 0
)
