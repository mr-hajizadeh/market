package com.se7en.market.Response.Cart

data class PaymentResponse(
    val status: String,
    val system_message: String,
    val `data`: Data,
    val messages: List<String>,
    val errors: List<Any>
) {
    data class Data(
        val payment_url: PaymentUrl,
        val order: Order
    ) {
        data class PaymentUrl(
            val action: String,
            val inputs: List<Any>,
            val method: String
        )

        data class Order(
            val seller_id: Int,
            val finalPrice: Int,
            val totalDiscount: Int,
            val delivery_cost: Int,
            val receiver_name: String,
            val receiver_phone: String,
            val destination_address: String,
            val destination_latitude: String,
            val destination_longitude: String,
            val user_id: Int,
            val updated_at: String,
            val created_at: String,
            val id: Int
        )
    }
}