package com.se7en.market.Model

data class ShopOrderModel(
    var shopName: String = "",
    var minPrice: Long = 0L,
    val items: MutableList<ProductModel>
)
