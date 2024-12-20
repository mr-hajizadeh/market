package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Repository.ShopRepository

class ShopViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ShopRepository()
    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val token = preferencesRepository.getToken()

    val itemsStatus get() = repository.itemsStatus
    val categoriesStatus get() = repository.categoriesStatus
    val categoryItemStatus get() = repository.categoryItemStatus

    fun getShopItems(shopId: Int) =
        repository.getShopItems("Bearer $token", shopId)


    fun getShopCategories(shopId: Int) =
        repository.getShopCategories("Bearer $token", shopId)


    fun getCategoryItems(categoryId: Int) =
        repository.getCategoryItems("Bearer $token", categoryId)

}