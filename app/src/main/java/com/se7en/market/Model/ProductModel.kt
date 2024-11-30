package com.se7en.market.Model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

interface ListItemModel

data class ProductModel(
    var id: Int = 0,
    var percent: String,
    var count: Int = 0,
    var loading: Boolean = false,
) : ListItemModel

data class CategoryModel(
    var image: Int,
    var title: String,
) : ListItemModel

data class ShopModel(
    var id: Int = 0,
    var user_id: Int = 0,
    var shop_name: String = "",
    var shop_address: String = "",
    var latitude: String = "",
    var longitude: String = "",
    var service_distance: Int = 0,
    var courier_fee: Int = 0,
    var delivery_time: Int = 0,
    var created_at: String = "",
    var updated_at: String = "",
    var distance: Double = 0.0,
    var formatted_distance: String = "",
) : ListItemModel {
    fun formatPriceToPersianCourierFee(): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("fa", "IR"))
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        val formattedPrice = decimalFormat.format(courier_fee)

        val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        var persianPrice = formattedPrice
        for (i in 0..9) {
            persianPrice = persianPrice.replace(i.toString(), persianDigits[i])
        }

        return persianPrice
    }
}

data class BannerModel(
    var bannerId: Int,
    var imageUrl: String,
) : ListItemModel
