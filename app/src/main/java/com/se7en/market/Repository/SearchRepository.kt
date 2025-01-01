package com.se7en.market.Repository

import com.se7en.market.Model.SearchTags
import com.se7en.market.Network.RetrofitService

class SearchRepository {

    fun globalSearch(token: String, searchTags: SearchTags) =
        RetrofitService.apiService().globalItemSearch(token, searchTags)
}