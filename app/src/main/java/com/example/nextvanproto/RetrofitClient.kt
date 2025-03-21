package com.example.nextvanproto

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
   private const val BASE_URL = "http://192.168.100.16/NextVan/"
   // private const val BASE_URL = "http://192.168.125.201/NextVan/"
   // private const val BASE_URL = "http://192.168.43.163/NextVan/"
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    val instance: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        retrofit.create(ApiService::class.java)
    }
}
