package com.se7en.market.Response.Cart

import com.se7en.market.Model.DiscountModel

data class GetAllDiscountCodes(
    val status: String,
    val system_message: String,
    val `data`: MutableList<DiscountModel>,
    val messages: List<String>,
    val errors: List<String>
)