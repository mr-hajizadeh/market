package com.se7en.market.Repository

import androidx.lifecycle.MutableLiveData
import com.se7en.market.Model.ShopModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Network.RetrofitService
import com.se7en.market.Response.Cart.CreateOrAddToCart
import com.se7en.market.Response.Cart.DeleteCart
import com.se7en.market.Response.Cart.GetAllCarts
import com.se7en.market.Response.Cart.GetCartDetails
import com.se7en.market.Response.Cart.PaymentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartRepository {

    private var _cardsStatus = MutableLiveData<NetworkResult<GetAllCarts>>()
    val cartsStatus get() = _cardsStatus

    private var _cartDetailStatus = MutableLiveData<NetworkResult<GetCartDetails>>()
    val cartDetailsStatus get() = _cartDetailStatus

    var shopList: MutableList<ShopModel>? = null
        private set

    companion object {
        @Volatile
        var INSTANCE: CartRepository? = null
        fun getInstance(): CartRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CartRepository().also { INSTANCE = it }
            }
        }
    }

    fun updateShopList(shopList: MutableList<ShopModel>) {
        this.shopList = shopList
    }

    fun createCartOrAddItem(
        token: String,
        mapData: Map<String, Int>,
    ): MutableLiveData<NetworkResult<CreateOrAddToCart>> {
        val addItemStatus = MutableLiveData<NetworkResult<CreateOrAddToCart>>()
        addItemStatus.value = NetworkResult.Loading
        RetrofitService.apiService().createCartOrAddItem(token, mapData)
            .enqueue(object : Callback<CreateOrAddToCart> {
                override fun onResponse(
                    call: Call<CreateOrAddToCart>,
                    response: Response<CreateOrAddToCart>,
                ) {
                    if (response.isSuccessful && response.body()?.status == "success")
                        addItemStatus.value = NetworkResult.Success(response.body()!!)
                    else if (response.code() == 422 || response.code() == 404)
                        addItemStatus.value =
                            NetworkResult.Error("محصول معتبر نمی باشد.")
                    else
                        addItemStatus.value =
                            NetworkResult.Error(response.body()?.errors?.get(0).toString())

                }

                override fun onFailure(call: Call<CreateOrAddToCart>, t: Throwable) {
                    addItemStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
        return addItemStatus
    }

    fun getAllCarts(token: String) {
        _cardsStatus.value = NetworkResult.Loading
        RetrofitService.apiService().getAllCarts(token)
            .enqueue(object : Callback<GetAllCarts> {
                override fun onResponse(call: Call<GetAllCarts>, response: Response<GetAllCarts>) {
                    if (response.isSuccessful)
                        _cardsStatus.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GetAllCarts>, t: Throwable) {
                    _cardsStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

    fun getCartDetails(token: String, cartId: Int) {
        _cartDetailStatus.value = NetworkResult.Loading
        RetrofitService.apiService().getCartDetails(token, cartId)
            .enqueue(object : Callback<GetCartDetails> {
                override fun onResponse(
                    call: Call<GetCartDetails>,
                    response: Response<GetCartDetails>,
                ) {
                    if (response.code() == 404)
                        _cartDetailStatus.value = NetworkResult.Error("fail")
                    if (response.isSuccessful && response.body()?.status == "success")
                        _cartDetailStatus.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GetCartDetails>, t: Throwable) {
                    _cartDetailStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

    fun deleteCart(token: String, cartId: Int): MutableLiveData<NetworkResult<DeleteCart>> {
        val deleteCartStatus = MutableLiveData<NetworkResult<DeleteCart>>()
        deleteCartStatus.value = NetworkResult.Loading
        RetrofitService.apiService().deleteCart(token, cartId)
            .enqueue(object : Callback<DeleteCart> {
                override fun onResponse(call: Call<DeleteCart>, response: Response<DeleteCart>) {
                    if (response.isSuccessful && response.body()?.status == "success")
                        deleteCartStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        deleteCartStatus.value =
                            NetworkResult.Error(response.body()?.errors?.get(0).toString())
                }

                override fun onFailure(call: Call<DeleteCart>, t: Throwable) {
                    deleteCartStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
        return deleteCartStatus
    }

    fun incrementItemQuantity(
        token: String,
        cartId: Int,
        itemId: Int,
    ): MutableLiveData<NetworkResult<GetCartDetails>> {
        val incrementItemStatus = MutableLiveData<NetworkResult<GetCartDetails>>()
        incrementItemStatus.value = NetworkResult.Loading
        RetrofitService.apiService().incrementItemQuantity(token, cartId, itemId)
            .enqueue(object : Callback<GetCartDetails> {
                override fun onResponse(
                    call: Call<GetCartDetails>,
                    response: Response<GetCartDetails>,
                ) {
                    if (response.isSuccessful) {
                        incrementItemStatus.value = NetworkResult.Success(response.body()!!)
                    }
                }

                override fun onFailure(call: Call<GetCartDetails>, t: Throwable) {
                    incrementItemStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
        return incrementItemStatus
    }

    fun decrementItemQuantity(
        token: String,
        cartId: Int,
        itemId: Int,
    ): MutableLiveData<NetworkResult<GetCartDetails>> {
        val decrementItemStatus = MutableLiveData<NetworkResult<GetCartDetails>>()
        decrementItemStatus.value = NetworkResult.Loading
        RetrofitService.apiService().decrementItemQuantity(token, cartId, itemId)
            .enqueue(object : Callback<GetCartDetails> {
                override fun onResponse(
                    call: Call<GetCartDetails>,
                    response: Response<GetCartDetails>,
                ) {
                    if (response.isSuccessful)
                        decrementItemStatus.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GetCartDetails>, t: Throwable) {
                    decrementItemStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
        return decrementItemStatus
    }

    fun setDiscountCode(
        token: String,
        cartId: Int,
        mapData: Map<String, String>
    ): MutableLiveData<NetworkResult<GetCartDetails>> {

        val discountCode = MutableLiveData<NetworkResult<GetCartDetails>>()
        discountCode.value = NetworkResult.Loading
        RetrofitService.apiService().setDiscountCode(token, cartId, mapData)
            .enqueue(object : Callback<GetCartDetails> {
                override fun onResponse(
                    call: Call<GetCartDetails>,
                    response: Response<GetCartDetails>
                ) {
                    if (response.isSuccessful && response.body() != null)
                        discountCode.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GetCartDetails>, t: Throwable) {
                    discountCode.value = NetworkResult.Error("Error ${t.message}")
                }
            })

        return discountCode

    }

    fun unsetDiscountCode(
        token: String,
        cartId: Int,
    ): MutableLiveData<NetworkResult<GetCartDetails>> {

        val discountCode = MutableLiveData<NetworkResult<GetCartDetails>>()
        discountCode.value = NetworkResult.Loading
        RetrofitService.apiService().unsetDiscountCode(token, cartId)
            .enqueue(object : Callback<GetCartDetails> {
                override fun onResponse(
                    call: Call<GetCartDetails>,
                    response: Response<GetCartDetails>
                ) {
                    if (response.isSuccessful && response.body() != null)
                        discountCode.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GetCartDetails>, t: Throwable) {
                    discountCode.value = NetworkResult.Error("Error ${t.message}")
                }
            })

        return discountCode

    }

    fun payment(token: String, cartId: Int): MutableLiveData<NetworkResult<PaymentResponse>> {
        val paymentStatus = MutableLiveData<NetworkResult<PaymentResponse>>()
        paymentStatus.value = NetworkResult.Loading
        RetrofitService.apiService().payment(
            token,
            cartId,
            mapOf("receiver_name" to "حاجی", "receiver_phone" to "09379871359")
        )
            .enqueue(object : Callback<PaymentResponse> {
                override fun onResponse(
                    call: Call<PaymentResponse>,
                    response: Response<PaymentResponse>
                ) {
                    if (response.isSuccessful)
                        paymentStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        paymentStatus.value = NetworkResult.Error("Error")
                }

                override fun onFailure(call: Call<PaymentResponse>, t: Throwable) {
                    paymentStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
        return paymentStatus
    }

}