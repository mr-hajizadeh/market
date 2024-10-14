package com.se7en.market.Network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitService {

    private fun getInstance(baseUrl: String = "https://api.neshan.org"): Retrofit {

        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client =
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

        return Retrofit.Builder()
            .baseUrl("https://api.neshan.org")
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()

    }


    fun apiService(baseUrl: String = ""): ApiService {
        return getInstance(baseUrl).create(ApiService::class.java)
    }

}