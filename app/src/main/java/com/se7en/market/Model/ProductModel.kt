package com.se7en.market.Model

interface ListItemModel

data class ProductModel(
    var id: Int = 0,
    var percent: String,
    var count: Int = 0,
    var loading: Boolean = false,
) : ListItemModel

data class CategoryModel(
    var image: Int,
    var title: String
) : ListItemModel

data class ShopModel(
    var id: Int = 0,
    var name: String = ""
) : ListItemModel

data class BannerModel(
    var bannerId: Int,
    var imageUrl: String
) : ListItemModel
