package com.se7en.market.Model

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

class DiscountModel(
    var id: Int = 0,
    var user_id: Int = 0,
    var title: String = "",
    var amount: Int = 0,
    var code: String? = null,
    var used: Boolean = false,
    var description: String? = null,
    var expiration: String = "",
    var created_at: String = "",
    var updated_at: String = ""
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