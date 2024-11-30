package com.se7en.market.Network

import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.MainListModel
import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.Model.SliderModel
import com.se7en.market.Model.UserModel
import com.se7en.market.Response.Address.AddressList
import com.se7en.market.Response.Address.DeleteAddress
import com.se7en.market.Response.Address.StoreAddress
import com.se7en.market.Response.Home.HomeResponse
import com.se7en.market.Response.OTP.Code
import com.se7en.market.Response.OTP.Confirm
import com.se7en.market.Response.OTP.Token
import com.se7en.market.Response.User.UpdateProfile
import com.se7en.market.Response.User.Profile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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
    fun confirmCode(
        @Header("token") token: String,
        @Body codeMap: Map<String, String>
    ): Call<Confirm>

    @GET("/api/v1/customer/profile")
    fun getProfile(@Header("Authorization") token: String): Call<Profile>

    @POST("/api/v1/customer/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body user: UserModel
    ): Call<UpdateProfile>

    @POST("/api/v1/customer/address")
    fun storeAddress(
        @Header("Authorization") token: String,
        @Body address: AddressModel
    ): Call<StoreAddress>

    @GET("/api/v1/customer/address")
    fun getAddressList(@Header("Authorization") token: String): Call<AddressList>

    @DELETE("/api/v1/customer/address/{id}")
    fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Call<DeleteAddress>

    @PUT("/api/v1/customer/address/{id}")
    fun editAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body address: AddressModel
    ): Call<StoreAddress>

    @GET("/api/v1/admin/slider")
    fun getSliderList(): Call<List<SliderModel>>

    @POST("/api/v1/customer/home")
    fun getAllData(
        @Header("Authorization") token: String,
        @Body latLon: Map<String, Double>
    ): Call<HomeResponse>

}