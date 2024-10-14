
package com.se7en.market.Model
data class ReverseGeocoding (
    val status: String,
    val formatted_address: String,
    val route_name: String,
    val route_type: String,
    val neighbourhood: String,
    val city: String,
    val state: String,
    val place: Any,
    val municipality_zone: String,
    val in_traffic_zone: Boolean,
    val in_odd_even_zone: Boolean,
    val village: Any,
    val county: String,
    val district: String
)
