package com.se7en.market.Response.Home

import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.ProductModel
import com.se7en.market.Model.ShopModel

data class HomeResponse(
    val status: String,
    val system_message: String,
    val `data`: Data,
    val messages: List<String>,
    val errors: List<String>
) {
    data class Data(
        val shops: MutableList<ShopModel>,
        val categories: MutableList<CategoryModel?>,
        val populars: MutableList<ProductModel>,
        val offers: MutableList<ProductModel>,
    )
}