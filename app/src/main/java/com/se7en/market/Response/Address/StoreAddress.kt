package com.se7en.market.Response.Address

import com.se7en.market.Model.AddressModel

data class StoreAddress(
    val status: String,
    val system_message: String,
    val `data`: AddressModel,
    val messages: List<String>,
    val errors: List<Any>
)