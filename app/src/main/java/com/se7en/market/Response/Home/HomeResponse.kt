package com.se7en.market.Response.Home

import com.se7en.market.Model.CategoryModel
import com.se7en.market.Model.ShopModel

data class HomeResponse(
    val status: String,
    val system_message: String,
    val `data`: Data,
    val messages: List<Any>,
    val errors: List<Any>
) {
    data class Data(
        val shops: MutableList<ShopModel>,
        val categories: MutableList<CategoryModel?>
    )
}