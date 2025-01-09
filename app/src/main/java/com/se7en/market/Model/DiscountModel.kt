package com.se7en.market.Model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class DiscountModel(
    val id: Int,
    val user_id: Int,
    val title: String,
    val amount: Int,
    val code: String,
    val used: Boolean,
    val description: String? = null,
    val expiration: String,
    val created_at: String,
    val updated_at: String
) {
    fun formatAmountToPersianAmount(): String {
        val decimalFormatSymbols = DecimalFormatSymbols(Locale("fa", "IR"))
        val decimalFormat = DecimalFormat("#,###", decimalFormatSymbols)
        val formattedPrice = decimalFormat.format((amount))

        val persianDigits = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
        var persianPrice = formattedPrice
        for (i in 0..9) {
            persianPrice = persianPrice.replace(i.toString(), persianDigits[i])
        }
        return persianPrice
    }
}