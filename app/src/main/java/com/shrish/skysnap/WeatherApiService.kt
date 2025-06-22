package com.shrish.skysnap

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("forecast.json")
    fun getWeatherData(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 1,
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): Call<WeatherResponse>
}
