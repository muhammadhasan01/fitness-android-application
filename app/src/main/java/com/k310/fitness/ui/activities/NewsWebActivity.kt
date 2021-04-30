package com.k310.fitness.ui.activities

import android.os.Bundle
import android.util.Log
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import com.k310.fitness.databinding.FragmentNewsWebviewBinding
import com.k310.fitness.util.Constants.EXTRA_MESSAGE

class NewsWebActivity : AppCompatActivity() {
    private lateinit var binding: FragmentNewsWebviewBinding
    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentNewsWebviewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        webView = binding.webViewNews

        val message = intent.getStringExtra(EXTRA_MESSAGE)
        if (message != null) {
            webView.loadUrl(message)
        }
    }
}