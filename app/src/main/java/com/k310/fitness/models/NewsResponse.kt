package com.k310.fitness.models

import com.google.gson.annotations.SerializedName

class NewsResponse {
    @SerializedName("status")
    lateinit var status: String
    @SerializedName("totalResults")
    val totalResults = 0
    @SerializedName("articles")
    lateinit var articles: List<News>
}