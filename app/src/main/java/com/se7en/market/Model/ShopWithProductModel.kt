package com.se7en.market.Model

data class ShopWithProductModel(
    var shop: ShopModel,
    var productItems: MutableList<ListItemModel>,
)