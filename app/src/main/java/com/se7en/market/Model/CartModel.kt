package com.se7en.market.Model

data class CartModel(
    var id: Int = 0,
    var seller_id: Int = 0,
    var user_id: Int = 0,
    var address_id: Int = 0,
    var discount_id: Int = 0,
    var description: String = "",
    var created_at: String = "",
    var updated_at: String = "",
    var total_price: Long = 0,
    var final_price: Long = 0,
    var courier_fee: Long = 0,
    var discount_price: Long = 0,
    var total_off: Long = 0,
    var seller: ShopModel = ShopModel(),
    var card_details: MutableList<CartDetailModel> = mutableListOf(),
    var loading: Boolean = false,
)
