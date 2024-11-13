package com.se7en.market.Network

sealed class NetworkResult<out T> {
    data object Loading : NetworkResult<Nothing>()
    data class Success<out T>(val response: T) :NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
}