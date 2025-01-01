package com.se7en.market.Model

data class SearchTags(
    val like: List<String>,
    val latitude: Double,
    val longitude: Double,
)