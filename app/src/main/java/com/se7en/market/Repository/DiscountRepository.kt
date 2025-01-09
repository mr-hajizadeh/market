package com.se7en.market.Repository

import com.se7en.market.Network.RetrofitService

class DiscountRepository {

    fun getAllDiscountCodes(token: String) =
        RetrofitService.apiService().getAllDiscountCodes(token)

}