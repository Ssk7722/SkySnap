package com.shrish.skysnap

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Query

object RetrofitInstance {

    private const val BASE_URL = "https://api.weatherapi.com/v1/"
    private const val API_KEY = "d189a34eefc94e11b1b120814252006" // 👈 Paste your key here, your grace

    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherApiService::class.java)
    }
}
