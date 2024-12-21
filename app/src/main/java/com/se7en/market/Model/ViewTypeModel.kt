package com.se7en.market.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
enum class ViewTypeModel : Parcelable {
    CATEGORY_TYPE,
    HEADER_TYPE,
    TWO_ROW_TYPE,
    ONE_ROW_TYPE,
    SHOP_TYPE,
    BRAND_TYPE,
}