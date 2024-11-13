package com.se7en.market.ViewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.se7en.market.Model.AddressModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.AddressRepository
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Response.Address.AddressList
import com.se7en.market.Response.Address.DeleteAddress
import com.se7en.market.Response.Address.StoreAddress
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddressViewModel(application: Application) : AndroidViewModel(application) {

    private var _storeStatus = MutableLiveData<NetworkResult<StoreAddress>>()
    val storeStatus = _storeStatus

    private var _listStatus = MutableLiveData<NetworkResult<AddressList>>()
    val listStatus = _listStatus

    private var _deleteStatus = MutableLiveData<NetworkResult<DeleteAddress>>()
    val deleteStatus = _deleteStatus

    private var _editStatus = MutableLiveData<NetworkResult<StoreAddress>>()
    val editStatus = _editStatus

    private val repository = AddressRepository()
    private val preferencesRepository = PreferencesRepository.getInstance(application)
    val token = preferencesRepository.getToken()

    fun storeAddress(address: AddressModel) {
        _storeStatus.value = NetworkResult.Loading
        repository.storeAddress("Bearer $token", address)
            .enqueue(object : Callback<StoreAddress> {
                override fun onResponse(
                    call: Call<StoreAddress>,
                    response: Response<StoreAddress>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success")
                        _storeStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _storeStatus.value =
                            NetworkResult.Error(response.body()?.messages?.get(0).toString())
                }

                override fun onFailure(call: Call<StoreAddress>, t: Throwable) {
                    _storeStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

    fun getAddressList() {
        viewModelScope.launch {
            _listStatus.value = NetworkResult.Loading
            repository.getAddressList("Bearer $token").enqueue(object : Callback<AddressList> {
                override fun onResponse(call: Call<AddressList>, response: Response<AddressList>) {
                    if (response.isSuccessful && response.body()?.status == "success")
                        _listStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _listStatus.value =
                            NetworkResult.Error(response.body()?.errors?.get(0).toString())
                }

                override fun onFailure(call: Call<AddressList>, t: Throwable) {
                    _listStatus.value = NetworkResult.Error("Error => ${t.message}")
                }
            })
        }
    }

    fun deleteAddress(id: Int) {
        viewModelScope.launch {
            _deleteStatus.value = NetworkResult.Loading
            repository.deleteAddress("Bearer $token", id).enqueue(object : Callback<DeleteAddress> {
                override fun onResponse(
                    call: Call<DeleteAddress>,
                    response: Response<DeleteAddress>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success")
                        _deleteStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _deleteStatus.value =
                            NetworkResult.Error(response.body()?.errors?.get(0).toString())
                }

                override fun onFailure(call: Call<DeleteAddress>, t: Throwable) {
                    _deleteStatus.value =
                        NetworkResult.Error("Error ${t.message}")
                }
            })
        }
    }

    fun saveActiveAddress(address: AddressModel) {
        preferencesRepository.saveActiveAddress(address)
    }

    fun getActiveAddress() = preferencesRepository.loadActiveAddress()

    fun editAddress(address: AddressModel) {
        _editStatus.value = NetworkResult.Loading
        repository.editAddress("Bearer $token", address.id, address)
            .enqueue(object : Callback<StoreAddress> {
                override fun onResponse(
                    call: Call<StoreAddress>,
                    response: Response<StoreAddress>
                ) {
                    if (response.isSuccessful && response.body()?.status == "success")
                        _editStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _editStatus.value =
                            NetworkResult.Error(response.body()?.messages?.get(0).toString())
                }

                override fun onFailure(call: Call<StoreAddress>, t: Throwable) {
                    _editStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

}