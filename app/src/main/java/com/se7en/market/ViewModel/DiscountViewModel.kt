package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.DiscountRepository
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Response.Cart.GetAllDiscountCodes
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiscountViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val repository = DiscountRepository()
    private val token = preferencesRepository.getToken()

    private var _discountStatus = MutableLiveData<NetworkResult<GetAllDiscountCodes>>()
    val discountStatus = _discountStatus

    fun getAllDiscountCodes() {
        _discountStatus.value = NetworkResult.Loading
        repository.getAllDiscountCodes("Bearer $token")
            .enqueue(object : Callback<GetAllDiscountCodes> {
                override fun onResponse(
                    call: Call<GetAllDiscountCodes>,
                    response: Response<GetAllDiscountCodes>
                ) {
                    if (response.isSuccessful) {
                        when (response.code()) {
                            200 -> {
                                if (response.body()?.status == "success")
                                    _discountStatus.value = NetworkResult.Success(response.body()!!)
                                else
                                    _discountStatus.value =
                                        NetworkResult.Error(response.body()?.errors!![0])
                            }

                            404 -> {
                                _discountStatus.value = NetworkResult.Error("not found")
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<GetAllDiscountCodes>, t: Throwable) {
                    _discountStatus.value = NetworkResult.Error(t.message.toString())
                }

            })
    }
}