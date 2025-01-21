package com.se7en.market.Network

import android.util.Log
import com.se7en.market.Utils.Constance.Companion.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class RetrofitService {

    companion object {

        private fun getInstance(baseUrl: String): Retrofit {

            val loggingInterceptor = HttpLoggingInterceptor { message ->
                Log.i(
                    "TAG",
                    "RetrofitService log: $message"
                )
            }.apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client =
                OkHttpClient.Builder()
                    .connectTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
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