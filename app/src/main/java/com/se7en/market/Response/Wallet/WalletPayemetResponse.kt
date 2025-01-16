package com.se7en.market.Response.Wallet

data class WalletPayemetResponse(
    val status: String,
    val system_message: String,
    val `data`: Data,
    val messages: List<String>,
    val errors: List<String>
) {
    data class Data(
        val payment_url: PaymentUrl
    ) {
        data class PaymentUrl(
            val action: String,
            val inputs: List<Any>,
            val method: String
        )
    }
}