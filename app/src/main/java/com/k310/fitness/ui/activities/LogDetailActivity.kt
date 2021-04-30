package com.k310.fitness.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.k310.fitness.databinding.FragmentLogDetailBinding

class LogDetailActivity : AppCompatActivity() {
    private lateinit var binding: FragmentLogDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentLogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val trainingType = intent.getStringExtra("trainingType")
        val startTime =  intent.getStringExtra("startTime")
        val stopTime = intent.getStringExtra("stopTime")
        val detail = intent.getStringExtra("detail")
        val duration = intent.getStringExtra("duration")
        val avgInKmh = intent.getStringExtra("avgInKmh")

        binding.detailTrainingTypeValue.text = trainingType
        binding.detailStartTimeValue.text = startTime
        binding.detailEndTimeValue.text = stopTime
        binding.detailInfo.text = detail
        binding.detailDuration.text = duration
        binding.detailAvgKmh.text = avgInKmh
    }
}