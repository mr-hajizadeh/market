package com.se7en.market.Repository

import com.se7en.market.Model.AddressModel
import com.se7en.market.Network.RetrofitService

class AddressRepository {

    fun storeAddress(token: String, address: AddressModel) =
        RetrofitService.apiService().storeAddress(token, address)

    fun getAddressList(token: String) = RetrofitService.apiService().getAddressList(token)

    fun deleteAddress(token: String, id: Int) =
        RetrofitService.apiService().deleteAddress(token, id)

    fun editAddress(token: String, id: Int, address: AddressModel) =
        RetrofitService.apiService().editAddress(token, id, address)

}