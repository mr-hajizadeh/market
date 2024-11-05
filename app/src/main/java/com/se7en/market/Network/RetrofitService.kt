package com.se7en.market.Network

import com.se7en.market.Utils.Constance.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitService {

    companion object {

        private fun getInstance(baseUrl: String): Retrofit {

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client =
                OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build()

            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        }

        fun apiService(baseUrl: String = BASE_URL): ApiService {
            return getInstance(baseUrl).create(ApiService::class.java)
        }
    }


}