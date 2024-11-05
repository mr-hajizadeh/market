package com.se7en.market.Network

import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.Response.OTP.Code
import com.se7en.market.Response.OTP.Confirm
import com.se7en.market.Response.OTP.Token
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @Headers("Api-Key: service.b8ad9848b3614526bcd2dfb35fae9b14")
    @GET("/v5/reverse")
    fun reverseGeocoding(
        @Query("lat") lat: Double,
        @Query("lng") lon: Double
    ): Call<ReverseGeocoding>

    @POST("/api/v1/customer/auth/otp")
    fun sendPhone(@Body phoneMap: Map<String, String>): Call<Token>


    @GET("/api/v1/customer/auth/otp/return")
    fun otpCode(@Header("token") token: String): Call<Code>

    @POST("/api/v1/customer/auth/otp/confirm")
    fun confirmCode(@Header("token") token: String,
                    @Body codeMap: Map<String, String>): Call<Confirm>

}