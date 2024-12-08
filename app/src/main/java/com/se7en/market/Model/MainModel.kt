@file:Suppress("DEPRECATED_ANNOTATION")

package com.se7en.market.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

interface ListItemModel

data class ProductModel(
    var id: Int = 0,
    var seller_id: Int = 0,
    var item_category_id: Int = 0,
    var title: String = "",
    var description: String = "",
    var amount: Long = 0,
    var off_percent: Int = 0,
    var image: List<String?> = emptyList(),
    var status: Boolean = false,
    var sort_order: Int = 0,
    var created_at: String = "",
    var updated_at: String = "",
    var count: Int = 0,
    var loading: Boolean = false,
) : ListItemModel {
    fun formatPriceToPersianAmount(): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("fa", "IR"))
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        val formattedPrice = decimalFormat.format(amount)

        val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        var persianPrice = formattedPrice
        for (i in 0..9) {
            persianPrice = persianPrice.replace(i.toString(), persianDigits[i])
        }

        return persianPrice
    }
}

@Parcelize
data class CategoryModel(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val image: String = "",
    val tags: List<String> = listOf(),
    val status: Int = 0,
    val created_at: String = "",
    val updated_at: String = "",
) : ListItemModel, Parcelable

@Parcelize
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
) : ListItemModel, Parcelable {
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
