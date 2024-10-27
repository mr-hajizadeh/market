package com.se7en.market.Model

data class AddressModel(
    var title: String = "",
    var address: String = "",
    var active: Boolean = false,
    var lat: Double = 0.0,
    var lon: Double = 0.0
)
