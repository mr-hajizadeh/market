package com.se7en.market.Model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

data class OrderModel(
    val id: Int,
    val user_id: Int,
    val seller_id: Int,
    val finalPrice: Int,
    val totalDiscount: Int,
    val delivery_cost: Int,
    val receiver_name: String,
    val receiver_phone: String,
    val destination_address: String,
    val destination_latitude: Double,
    val destination_longitude: Double,
    val created_at: String,
    val updated_at: String,
    val status: String,
    val status_title: String,
    val seller: ShopModel,
    val order_details: MutableList<CartDetailModel>
) {
    fun formatPriceToPersian(): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("fa", "IR"))
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        val formattedPrice = decimalFormat.format(finalPrice)

        val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        var persianPrice = formattedPrice
        for (i in 0..9) {
            persianPrice = persianPrice.replace(i.toString(), persianDigits[i])
        }

        return persianPrice
    }

    fun getCreatedAtTimestamp(): Long? {
        return try {
            val dateFormat = java.text.SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'",
                Locale.getDefault()
            )
            dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
            val date = dateFormat.parse(created_at)
            date?.time
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
