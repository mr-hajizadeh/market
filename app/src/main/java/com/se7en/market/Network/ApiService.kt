package com.se7en.market.Network

import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.ReverseGeocoding
import com.se7en.market.Model.SliderModel
import com.se7en.market.Model.UserModel
import com.se7en.market.Response.Address.AddressList
import com.se7en.market.Response.Address.DeleteAddress
import com.se7en.market.Response.Address.StoreAddress
import com.se7en.market.Response.Cart.CreateOrAddToCart
import com.se7en.market.Response.Cart.DeleteCart
import com.se7en.market.Response.Cart.GetAllCarts
import com.se7en.market.Response.Cart.GetCartDetails
import com.se7en.market.Response.Home.GlobalCategoryItem
import com.se7en.market.Response.Home.HomeResponse
import com.se7en.market.Response.Neshan.DistanceMatrixResponse
import com.se7en.market.Response.OTP.Code
import com.se7en.market.Response.OTP.Confirm
import com.se7en.market.Response.OTP.Token
import com.se7en.market.Response.Seller.GetCategoryItems
import com.se7en.market.Response.Seller.SellerCategories
import com.se7en.market.Response.Seller.SellerItems
import com.se7en.market.Response.User.Profile
import com.se7en.market.Response.User.UpdateProfile
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @Headers("Api-Key: service.b8ad9848b3614526bcd2dfb35fae9b14")
    @GET("/v5/reverse")
    fun reverseGeocoding(
        @Query("lat") lat: Double,
        @Query("lng") lon: Double,
    ): Call<ReverseGeocoding>

    @Headers("Api-Key: service.b8ad9848b3614526bcd2dfb35fae9b14")
    @GET("v1/distance-matrix")
    fun getDistanceMatrix(
        @Query("type") type: String,
        @Query("origins") origins: String,
        @Query("destinations") destinations: String
    ): Call<DistanceMatrixResponse>

    @Headers("Accept: application/json")
    @POST("/api/v1/customer/auth/otp")
    fun sendPhone(@Body phoneMap: Map<String, String>): Call<Token>

    @Headers("Accept: application/json")
    @GET("/api/v1/customer/auth/otp/return")
    fun otpCode(@Header("token") token: String): Call<Code>

    @Headers("Accept: application/json")
    @POST("/api/v1/customer/auth/otp/confirm")
    fun confirmCode(
        @Header("token") token: String,
        @Body codeMap: Map<String, String>,
    ): Call<Confirm>

    @Headers("Accept: application/json")
    @GET("/api/v1/customer/profile")
    fun getProfile(@Header("Authorization") token: String): Call<Profile>

    @Headers("Accept: application/json")
    @POST("/api/v1/customer/profile")
    fun updateProfile(
        @Header("Authorization") token: String,
        @Body user: UserModel,
    ): Call<UpdateProfile>

    @Headers("Accept: application/json")
    @POST("/api/v1/customer/address")
    fun storeAddress(
        @Header("Authorization") token: String,
        @Body address: AddressModel,
    ): Call<StoreAddress>

    @Headers("Accept: application/json")
    @GET("/api/v1/customer/address")
    fun getAddressList(@Header("Authorization") token: String): Call<AddressList>

    @Headers("Accept: application/json")
    @DELETE("/api/v1/customer/address/{id}")
    fun deleteAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
    ): Call<DeleteAddress>

    @Headers("Accept: application/json")
    @PUT("/api/v1/customer/address/{id}")
    fun editAddress(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body address: AddressModel,
    ): Call<StoreAddress>

    @Headers("Accept: application/json")
    @GET("/api/v1/admin/slider")
    fun getSliderList(@Header("Authorization") token: String): Call<List<SliderModel>>

    @Headers("Accept: application/json")
    @POST("/api/v1/customer/home")
    fun getAllData(
        @Header("Authorization") token: String,
        @Body latLon: Map<String, Double>,
    ): Call<HomeResponse>

    @Headers("Accept: application/json")
    @POST("/api/v1/customer/globalCategories/{categoryId}/sellers/items")
    fun getGlobalCategoryItems(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: Int,
        @Body latLon: Map<String, Double>,
    ): Call<GlobalCategoryItem>

    @Headers("Accept: application/json")
    @GET("/api/v1/seller/{shopId}/items")
    fun getShopItems(
        @Header("Authorization") token: String,
        @Path("shopId") shopId: Int,
    ): Call<SellerItems>

    @Headers("Accept: application/json")
    @GET("/api/v1/seller/categories/{categoryId}/items")
    fun getCategoryItems(
        @Header("Authorization") token: String,
        @Path("categoryId") categoryId: Int,
    ): Call<GetCategoryItems>

    @Headers("Accept: application/json")
    @GET("/api/v1/seller/{shopId}/categories")
    fun getShopCategories(
        @Header("Authorization") token: String,
        @Path("shopId") shopId: Int,
    ): Call<SellerCategories>

    @Headers("Accept: application/json")
    @POST("/api/v1/customer/card")
    fun createCartOrAddItem(
        @Header("Authorization") token: String,
        @Body mapData: Map<String, Int>,
    ): Call<CreateOrAddToCart>

    @Headers("Accept: application/json")
    @GET("/api/v1/customer/card")
    fun getAllCarts(@Header("Authorization") token: String): Call<GetAllCarts>

    @Headers("Accept: application/json")
    @GET("/api/v1/customer/card/{cartId}")
    fun getCartDetails(
        @Header("Authorization") token: String,
        @Path("cartId") cartId: Int,
    ): Call<GetCartDetails>

    @Headers("Accept: application/json")
    @DELETE("/api/v1/customer/card/{cartId}")
    fun deleteCart(
        @Header("Authorization") token: String,
        @Path("cartId") cartId: Int,
    ): Call<DeleteCart>

    @Headers("Accept: application/json")
    @PATCH("/api/v1/customer/card/{cartId}/items/{itemId}/increment")
    fun incrementItemQuantity(
        @Header("Authorization") token: String,
        @Path("cartId") cartId: Int,
        @Path("itemId") itemId: Int,
    ): Call<GetCartDetails>

    @Headers("Accept: application/json")
    @PATCH("/api/v1/customer/card/{cartId}/items/{itemId}/decrement")
    fun decrementItemQuantity(
        @Header("Authorization") token: String,
        @Path("cartId") cartId: Int,
        @Path("itemId") itemId: Int,
    ): Call<GetCartDetails>

}