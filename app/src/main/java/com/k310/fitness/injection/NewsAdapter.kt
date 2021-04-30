package com.k310.fitness.injection

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.k310.fitness.R
import com.k310.fitness.models.News

class NewsAdapter(newsList: List<News>?) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private val newsList: List<News>? = newsList
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.news_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = newsList?.get(position)
        holder.txtNewsAuthor.text = news?.author
        holder.txtNewsDesc.text = news?.description
        holder.txtNewsTitle.text = news?.title
    }

    override fun getItemCount() = newsList?.size ?: 0

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtNewsDesc: TextView = itemView
            .findViewById(R.id.txt_news_desc) as TextView
        val txtNewsTitle: TextView = itemView
            .findViewById(R.id.txt_news_title) as TextView
        val txtNewsAuthor: TextView = itemView
            .findViewById(R.id.txt_news_author) as TextView
    }
}