package com.k310.fitness.ui.fragments

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k310.fitness.api.RetrofitClient
import com.k310.fitness.databinding.FragmentNewsBinding
import com.k310.fitness.injection.NewsAdapter
import com.k310.fitness.models.News
import com.k310.fitness.models.NewsResponse
import com.k310.fitness.ui.activities.NewsWebActivity
import com.k310.fitness.ui.viewmodels.NewsViewModel
import com.k310.fitness.util.Constants.EXTRA_MESSAGE
import com.k310.fitness.util.NewsItemClickListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private var newsList: ArrayList<News>? = null
    private val country = "id"
    private val category = "sports"
    private var curOrientation = 0
    private val apiKey: String = "4861e1ba910c48d280b2eff1e538f927"
    private lateinit var binding: FragmentNewsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentNewsBinding.inflate(inflater, container, false);
        return binding.root;
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
        binding.refreshLayoutNews.setOnRefreshListener { injectData(country, category, apiKey) }
        curOrientation = resources.configuration.orientation
        injectData(country, category, apiKey)
    }

    private fun injectData(country: String, category: String, apiKey: String) {
        val refreshLayout = binding.refreshLayoutNews
        refreshLayout.isRefreshing = true
        newsList = ArrayList()
        val call = RetrofitClient.instance.api.getAllNews(country, category, apiKey);
        call.enqueue(object : Callback<NewsResponse?> {
            override fun onResponse(call: Call<NewsResponse?>?, response: Response<NewsResponse?>) {
                refreshLayout.isRefreshing = false
                val listOfNews: List<News>? = response.body()?.articles
                if (listOfNews != null) {
                    for (i in listOfNews.indices) {
                        if (listOfNews[i].author == null) continue
                        if (listOfNews[i].title == null) continue
                        if (listOfNews[i].description == null) continue
                        if (listOfNews[i].url == null) continue
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

            override fun onFailure(call: Call<NewsResponse?>?, t: Throwable?) {
                refreshLayout.isRefreshing = false
                Toast.makeText(activity, t?.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        curOrientation = newConfig.orientation
        injectData(country, category, apiKey)
    }

    private fun hookingAdapter(listNews: List<News>) {
        val adapter = NewsAdapter(listNews)
        lateinit var layoutManager: RecyclerView.LayoutManager
        if (curOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            layoutManager = GridLayoutManager(activity, 2)
        } else {
            layoutManager = LinearLayoutManager(activity)
        }
        val recyclerNews = binding.recyclerNews

        recyclerNews.adapter = adapter
        recyclerNews.layoutManager = layoutManager

        recyclerNews.addOnItemTouchListener(
            NewsItemClickListener(
                activity,
                recyclerNews,
                object : NewsItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val news = listNews[position]
                        val intent = Intent(activity, NewsWebActivity::class.java).apply {
                            putExtra(EXTRA_MESSAGE, news.url)
                        }
                        startActivity(intent)
                    }
                })
        )
    }
}