package com.se7en.market.Repository

import com.se7en.market.Network.RetrofitService

class LoginRepository {


    fun sendPhone(phone: Map<String, String>) = RetrofitService.apiService().sendPhone(phone)

    fun otpCode(token: String) = RetrofitService.apiService().otpCode(token)

    fun confirmCode(token: String, code: Map<String, String>) =
        RetrofitService.apiService().confirmCode(token, code)

}