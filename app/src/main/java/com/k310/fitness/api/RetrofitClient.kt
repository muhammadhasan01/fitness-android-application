package com.k310.fitness.api

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

class RetrofitClient private constructor() {
    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val api: Api
        get() = retrofit.create(Api::class.java)

    companion object {
        private const val BASE_URL = "https://newsapi.org/v2/"
        private var mInstance = RetrofitClient()

        @get:Synchronized
        val instance: RetrofitClient
            get() {
                return mInstance
            }
    }

}