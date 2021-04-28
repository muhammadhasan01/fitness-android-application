package com.k310.fitness.api

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

class RetrofitClient private constructor() {
    private val retrofit: Retrofit
    val api: Api
        get() = retrofit.create(Api::class.java)

    companion object {
        private const val BASE_URL = "https://newsapi.org/v2/"
        private var mInstance: RetrofitClient? = null

        @get:Synchronized
        val instance: RetrofitClient?
            get() {
                if (mInstance == null) {
                    mInstance = RetrofitClient()
                }
                return mInstance
            }
    }

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}