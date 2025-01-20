@file:Suppress("DEPRECATED_ANNOTATION")

package com.se7en.market.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartDetailModel(
    var id: Int = 0,
    var card_id: Int = 0,
    var item_id: Int = 0,
    var quantity: Int = 0,
    var created_at: String = "",
    var updated_at: String = "",
    var total_price: Long = 0,
    var final_price: Long = 0,
    var item: ProductModel = ProductModel(),
    val order_id: Int = 0,
    val item_title: String = "",
    val fee: Int = 0,
    val off_price: Int = 0,
) : Parcelable
