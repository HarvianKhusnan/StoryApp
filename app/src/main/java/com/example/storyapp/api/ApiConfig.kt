package com.example.storyapp.api

import com.example.storyapp.BuildConfig.DEBUG
import com.example.storyapp.api.BaseUrl.URL_API
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiConfig {
    private val loggingInterceptor = if(DEBUG){
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }else {
        HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.NONE
        }
    }
    private val clientService = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    private val retrofitService = Retrofit.Builder()
        .baseUrl(URL_API)
        .addConverterFactory(GsonConverterFactory.create())
        .client(clientService)
        .build()
    val apiInstance: ApiService = retrofitService.create(ApiService::class.java)
}