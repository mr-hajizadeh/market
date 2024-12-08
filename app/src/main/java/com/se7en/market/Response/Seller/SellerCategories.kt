package com.se7en.market.Response.Seller

import com.se7en.market.Model.CategoryModel

data class SellerCategories(
    val status: String,
    val system_message: String,
    val `data`: List<CategoryModel>,
    val messages: List<Any>,
    val errors: List<Any>,
)