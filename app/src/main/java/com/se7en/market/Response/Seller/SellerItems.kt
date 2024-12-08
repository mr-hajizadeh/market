package com.se7en.market.Response.Seller

import com.se7en.market.Model.ProductModel

data class SellerItems(
    val status: String,
    val system_message: String,
    val `data`: MutableList<ProductModel>,
    val messages: List<String>,
    val errors: List<String>,
)