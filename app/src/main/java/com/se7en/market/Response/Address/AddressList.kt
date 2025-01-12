package com.se7en.market.Response.Address

import com.se7en.market.Model.AddressModel

data class AddressList(
    val status: String,
    val system_message: String,
    val `data`: MutableList<AddressModel>,
    val messages: List<String>,
    val errors: List<String>
)