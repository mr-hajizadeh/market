@file:Suppress("DEPRECATED_ANNOTATION")

package com.se7en.market.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

interface ListItemModel

@Parcelize
data class ProductModel(
    var id: Int = 0,
    var seller_id: Int = 0,
    var item_category_id: Int = 0,
    var title: String = "",
    var description: String = "",
    var amount: Long = 0,
    var off_percent: Int = 0,
    var image: List<String> = listOf(),
    var status: Boolean = false,
    var sort_order: Int = 0,
    var created_at: String = "",
    var updated_at: String = "",
    var quantity: Int = 0,
    var loading: Boolean = false,
    var quantityLoading: Boolean = false,
    var cartDetail: CartDetailModel? = null,
) : ListItemModel, Parcelable {
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
    val seller_id: Int = 0,
    val title: String = "",
    val description: String? = null,
    val image: String = "",
    val tags: List<String> = listOf(),
//    val status: Int = 0,
    val created_at: String = "",
    val updated_at: String = "",
    val sort_order: Int = 0,
) : ListItemModel, Parcelable

@Parcelize
data class ShopModel(
    var id: Int = 0,
    var user_id: Int = 0,
    var shop_name: String = "",
    var shop_address: String = "",
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var service_distance: Float = 0f,
    var courier_fee: Int = 0,
    var delivery_time: Int = 0,
    var created_at: String = "",
    var updated_at: String = "",
    var distance: Double = 0.0,
    var formatted_distance: String = "",
    val items: MutableList<ProductModel> = mutableListOf(),
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
