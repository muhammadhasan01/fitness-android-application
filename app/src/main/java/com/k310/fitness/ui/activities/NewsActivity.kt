package com.k310.fitness.ui.activities

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k310.fitness.R
import com.k310.fitness.api.RetrofitClient
import com.k310.fitness.injection.NewsAdapter
import com.k310.fitness.models.News
import com.k310.fitness.models.NewsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsActivity : MainActivity() {
    private lateinit var recyclerNews: RecyclerView
    private var newsList: ArrayList<News>? = null
    private val country = "id"
    private val category = "sports"
    private val API_KEY: String = "4861e1ba910c48d280b2eff1e538f927"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_news)

        injectData(country, category, API_KEY)
    }

    private fun injectData(country: String, category: String, API_KEY: String) {
        newsList = ArrayList()
        val call: Call<NewsResponse?>? = RetrofitClient.instance?.api?.getAllNews(country, category, API_KEY);
        call?.enqueue(object : Callback<NewsResponse?> {
            override fun onResponse(call: Call<NewsResponse?>?, response: Response<NewsResponse?>) {
                val listOfNews: List<News>? = response.body()?.articles
                if (listOfNews != null) {
                    for (i in listOfNews.indices) {
                        (newsList as ArrayList<News>).add(
                            News(
                                listOfNews[i].author,
                                listOfNews[i].title,
                                listOfNews[i].description,
                                listOfNews[i].url
                            )
                        )
                    }
                }
                hookingAdapter(newsList as ArrayList<News>)
            }

            override fun onFailure(call: Call<NewsResponse?>?, t: Throwable?) {}
        })
    }

    private fun hookingAdapter(listNews : List<News>) {
        recyclerNews = findViewById(R.id.recyclerNews)
        val adapter = NewsAdapter(newsList)
        val layoutManager : RecyclerView.LayoutManager = LinearLayoutManager(this)

        recyclerNews.adapter = adapter
        recyclerNews.layoutManager = layoutManager
        // TODO: Add an onClickListener to open a webView
    }
}