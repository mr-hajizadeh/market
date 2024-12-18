package com.se7en.market.Model

data class CartDetailModel(
    var id: Int = 0,
    var card_id: Int = 0,
    var item_id: Int = 0,
    var quantity: Int = 0,
    var created_at: String = "",
    var updated_at: String = "",
    var item: ProductModel = ProductModel(),
)
