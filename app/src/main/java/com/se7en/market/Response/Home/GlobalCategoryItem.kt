package com.se7en.market.Response.Home

import com.se7en.market.Model.ShopModel

data class GlobalCategoryItem(
    val status: String = "",
    val system_message: String = "",
    val `data`: List<ShopModel> = listOf(),
    val messages: List<String> = listOf(),
    val errors: List<String> = listOf(),
)