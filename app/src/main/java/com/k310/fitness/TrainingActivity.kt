package com.k310.fitness

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.k310.fitness.databinding.ActivityMainBinding
import com.k310.fitness.fragments.HistoryFragment
import com.k310.fitness.fragments.NewsFragment
import com.k310.fitness.fragments.ScheduleFragment
import com.k310.fitness.fragments.TrainingFragment


class TrainingActivity : MainActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        makeCurrentFragment(trainingFragment)

        // TODO start training immediately
    }
}