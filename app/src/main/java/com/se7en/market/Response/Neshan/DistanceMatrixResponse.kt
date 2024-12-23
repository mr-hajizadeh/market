package com.se7en.market.Response.Neshan

data class DistanceMatrixResponse(
    val status: String,
    val rows: List<Row>,
    val origin_addresses: List<String>,
    val destination_addresses: List<String>
) {
    data class Row(
        val elements: List<Element>
    ) {
        data class Element(
            val status: String,
            val duration: Duration,
            val distance: Distance
        ) {
            data class Duration(
                val value: Int,
                val text: String
            )

            data class Distance(
                val value: Int,
                val text: String
            )
        }
    }
}