package com.se7en.market.Network

import com.se7en.market.Model.ReverseGeocoding
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface ApiService {

    @Headers("Api-Key: service.b8ad9848b3614526bcd2dfb35fae9b14")
    @GET("/v5/reverse")
    fun reverseGeocoding(@Query("lat") lat: Double, @Query("lng") lon: Double): Call<ReverseGeocoding>

}