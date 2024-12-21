package com.se7en.market.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class MainListModel(
    val title: String = "",
    val position: Int = 0,
    val color: Int = 0,
    val viewType: ViewTypeModel = ViewTypeModel.HEADER_TYPE,
    val items: MutableList<ListItemModel> = mutableListOf(),
) : Parcelable