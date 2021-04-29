package com.k310.fitness.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k310.fitness.R
import com.k310.fitness.api.RetrofitClient
import com.k310.fitness.databinding.FragmentNewsBinding
import com.k310.fitness.injection.NewsAdapter
import com.k310.fitness.models.News
import com.k310.fitness.models.NewsResponse
import com.k310.fitness.ui.viewmodels.NewsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NewsFragment : Fragment() {
    private lateinit var viewModel: NewsViewModel
    private var newsList: ArrayList<News>? = null
    private val country = "id"
    private val category = "sports"
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
        Log.d("Some sheep", "Created")
        binding.refreshLayoutNews.setOnRefreshListener { injectData(country, category, apiKey) }
        injectData(country, category, apiKey)
    }

    private fun injectData(country: String, category: String, apiKey: String) {
        binding.refreshLayoutNews.isRefreshing = true
        newsList = ArrayList()
        val call = RetrofitClient.instance.api.getAllNews(country, category, apiKey);
        call.enqueue(object : Callback<NewsResponse?> {
            override fun onResponse(call: Call<NewsResponse?>?, response: Response<NewsResponse?>) {
                binding.refreshLayoutNews.isRefreshing = false
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
                Log.d("Some sheep", newsList.toString())
                hookingAdapter(newsList as ArrayList<News>)
            }

            override fun onFailure(call: Call<NewsResponse?>?, t: Throwable?) {
                binding.refreshLayoutNews.isRefreshing = false
                Toast.makeText(activity, t?.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun hookingAdapter(listNews: List<News>) {
        val adapter = NewsAdapter(listNews)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(activity)

        binding.recyclerNews.adapter = adapter
        binding.recyclerNews.layoutManager = layoutManager
        // TODO: Add an onClickListener to open a webView
    }
}