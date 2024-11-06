package com.se7en.market.Repository

import com.se7en.market.Network.RetrofitService

class ProfileRepository {

    fun getProfile(token: String) = RetrofitService.apiService().getProfile(token)

}