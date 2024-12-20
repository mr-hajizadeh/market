package com.se7en.market.Repository

import com.se7en.market.Network.RetrofitService

class CategoryRepository {

    fun getGlobalCategoryItem(token: String, categoryInt: Int, latLon: Map<String, Double>) =
        RetrofitService.apiService().getGlobalCategoryItems(token, categoryInt, latLon)

}