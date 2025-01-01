package com.se7en.market.Model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SliderModel(
    val id: Int,
    val title: String,
    val banner: String,
    val tags: List<String>,
    val created_at: String,
    val updated_at: String
) : Parcelable
