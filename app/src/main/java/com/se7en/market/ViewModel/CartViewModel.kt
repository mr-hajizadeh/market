package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.se7en.market.Model.ShopModel
import com.se7en.market.Repository.CartRepository
import com.se7en.market.Repository.PreferencesRepository

class CartViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CartRepository.getInstance()
    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val token = preferencesRepository.getToken()
    val cartDetailStatus get() = repository.cartDetailsStatus
    val cartsStatus get() = repository.cartsStatus

    fun createOrAddItemToCart(itemId: Int, addressId: Int) =
        repository.createCartOrAddItem(
            "Bearer $token",
            mapOf("item_id" to itemId, "address_id" to addressId)
        )

    fun getAllCarts() = repository.getAllCarts("Bearer $token")

    fun getCartDetails(cartId: Int) = repository.getCartDetails("Bearer $token", cartId)

    fun deleteCart(cartId: Int) = repository.deleteCart("Bearer $token", cartId)

    fun incrementItemQuantity(cartId: Int, itemId: Int) =
        repository.incrementItemQuantity("Bearer $token", cartId, itemId)

    fun decrementItemQuantity(cartId: Int, itemId: Int) =
        repository.decrementItemQuantity("Bearer $token", cartId, itemId)

    fun updateShopList(shopList: MutableList<ShopModel>) = repository.updateShopList(shopList)

    fun getShopById(shopId: Int): ShopModel? =
        repository.shopList?.find { it.id == shopId }?.let {
            return it
        }

    fun setDiscountCode(cartId: Int, mapData: Map<String, String>) =
        repository.setDiscountCode("Bearer $token", cartId, mapData)

    fun unsetDiscountCode(cartId: Int) =
        repository.unsetDiscountCode("Bearer $token", cartId)

}