package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.se7en.market.Model.SearchTags
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Repository.SearchRepository
import com.se7en.market.Response.Search.SearchResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private val token = preferencesRepository.getToken()
    private val repository = SearchRepository()

    private var _searchStatus = MutableLiveData<NetworkResult<SearchResponse>>()
    val searchStatus get() = _searchStatus

    fun globalSearch(searchTags: SearchTags) {
        _searchStatus.value = NetworkResult.Loading
        repository.globalSearch("Bearer $token", searchTags)
            .enqueue(object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    if (response.isSuccessful)
                        _searchStatus.value = NetworkResult.Success(response.body()!!)
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    _searchStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })
    }

}