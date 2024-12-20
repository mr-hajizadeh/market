package com.se7en.market.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.CategoryRepository
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Response.Home.GlobalCategoryItem
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val repository = CategoryRepository()
    private val token = preferencesRepository.getToken()

    private var _globalCategoryStatus = MutableLiveData<NetworkResult<GlobalCategoryItem>>()
    val globalCategoryStatus get() = _globalCategoryStatus

    fun loadActiveAddress() = preferencesRepository.loadActiveAddress()

    fun getGlobalCategoryItem(categoryInt: Int, latLon: Map<String, Double>) {
        Log.i("TAG", "getGlobalCategoryItem: $token")
        _globalCategoryStatus.value = NetworkResult.Loading
        repository.getGlobalCategoryItem("Bearer $token", categoryInt, latLon)
            .enqueue(object : Callback<GlobalCategoryItem> {
                override fun onResponse(
                    call: Call<GlobalCategoryItem>,
                    response: Response<GlobalCategoryItem>,
                ) {
                    if (response.isSuccessful)
                        _globalCategoryStatus.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GlobalCategoryItem>, t: Throwable) {
                    _globalCategoryStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

}