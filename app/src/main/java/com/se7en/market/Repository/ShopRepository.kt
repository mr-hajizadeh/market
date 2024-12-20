package com.se7en.market.Repository

import androidx.lifecycle.MutableLiveData
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Network.RetrofitService
import com.se7en.market.Response.Seller.GetCategoryItems
import com.se7en.market.Response.Seller.SellerCategories
import com.se7en.market.Response.Seller.SellerItems
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ShopRepository {

    private var _itemsStatus = MutableLiveData<NetworkResult<SellerItems>>()
    val itemsStatus get() = _itemsStatus

    private var _categoriesStatus = MutableLiveData<NetworkResult<SellerCategories>>()
    val categoriesStatus get() = _categoriesStatus

    private var _categoryItemStatus = MutableLiveData<NetworkResult<GetCategoryItems>>()
    val categoryItemStatus get() = _categoryItemStatus

    fun getShopItems(token: String, shopId: Int) {
        _itemsStatus.value = NetworkResult.Loading
        RetrofitService.apiService().getShopItems(token, shopId)
            .enqueue(object : Callback<SellerItems> {
                override fun onResponse(call: Call<SellerItems>, response: Response<SellerItems>) {
                    if (response.isSuccessful and response.body()?.status.equals("success"))
                        _itemsStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _itemsStatus.value =
                            NetworkResult.Error("Error ${response.body()?.errors?.get(0)}")
                }

                override fun onFailure(call: Call<SellerItems>, t: Throwable) {
                    _itemsStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

    fun getShopCategories(token: String, shopId: Int) {
        _categoriesStatus.value = NetworkResult.Loading
        RetrofitService.apiService().getShopCategories(token, shopId)
            .enqueue(object : Callback<SellerCategories> {
                override fun onResponse(
                    call: Call<SellerCategories>,
                    response: Response<SellerCategories>,
                ) {
                    if (response.isSuccessful and response.body()?.status.equals("success"))
                        _categoriesStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _categoriesStatus.value =
                            NetworkResult.Error("Error ${response.body()?.errors?.get(0)}")
                }

                override fun onFailure(call: Call<SellerCategories>, t: Throwable) {
                    _categoriesStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

    fun getCategoryItems(token: String, categoryId: Int) {
        _categoryItemStatus.value = NetworkResult.Loading
        RetrofitService.apiService().getCategoryItems(token, categoryId)
            .enqueue(object : Callback<GetCategoryItems> {
                override fun onResponse(
                    call: Call<GetCategoryItems>,
                    response: Response<GetCategoryItems>,
                ) {
                    if (response.isSuccessful)
                        _categoryItemStatus.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GetCategoryItems>, t: Throwable) {
                    _categoryItemStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

}