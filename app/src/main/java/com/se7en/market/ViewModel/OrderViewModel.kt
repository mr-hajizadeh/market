package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.OrderRepository
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Response.Neshan.DistanceMatrixResponse
import com.se7en.market.Response.Order.GetAllOrders
import com.se7en.market.Response.Order.GetOrderDetails
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = OrderRepository()
    private val preferencesRepository = PreferencesRepository.getInstance(application)
    val token = preferencesRepository.getToken()

    private var _distanceStatus = MutableLiveData<NetworkResult<DistanceMatrixResponse>>()
    val distanceStatus get() = _distanceStatus

    private var _ordersStatus = MutableLiveData<NetworkResult<GetAllOrders>>()
    val ordersStatus get() = _ordersStatus

    private var _orderDetailsStatus = MutableLiveData<NetworkResult<GetOrderDetails>>()
    val orderDetailsStatus get() = _orderDetailsStatus

    fun getAllOrders() {
        _ordersStatus.value = NetworkResult.Loading
        repository.getAllOrders("Bearer $token")
            .enqueue(object : Callback<GetAllOrders> {
                override fun onResponse(
                    call: Call<GetAllOrders>,
                    response: Response<GetAllOrders>
                ) {
                    if (response.isSuccessful)
                        _ordersStatus.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GetAllOrders>, t: Throwable) {
                    _ordersStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

    fun getDistanceMatrix(origins: String, distance: String) {
        _distanceStatus.value = NetworkResult.Loading
        repository.getDistanceMatrix("motorcycle", origins, distance)
            .enqueue(object : Callback<DistanceMatrixResponse> {
                override fun onResponse(
                    call: Call<DistanceMatrixResponse>,
                    response: Response<DistanceMatrixResponse>
                ) {
                    if (response.isSuccessful)
                        _distanceStatus.value = NetworkResult.Success(response.body()!!)
                    if (response.code() == 470)
                        _distanceStatus.value = NetworkResult.Error("invalid locations")
                }

                override fun onFailure(call: Call<DistanceMatrixResponse>, t: Throwable) {
                    _distanceStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

    fun getOrderDetails(orderId: Int) {
        _orderDetailsStatus.value = NetworkResult.Loading
        repository.getOrderDetails("Bearer $token", orderId)
            .enqueue(object : Callback<GetOrderDetails> {
                override fun onResponse(
                    call: Call<GetOrderDetails>,
                    response: Response<GetOrderDetails>
                ) {
                    if (response.isSuccessful)
                        _orderDetailsStatus.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<GetOrderDetails>, t: Throwable) {
                    _orderDetailsStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

}