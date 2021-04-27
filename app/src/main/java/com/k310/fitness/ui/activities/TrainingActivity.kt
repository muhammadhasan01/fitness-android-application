package com.k310.fitness.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.k310.fitness.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrainingActivity : MainActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        makeCurrentFragment(trainingFragment)

        // TODO start training immediately
        // harusnya gabung mainactivity, trs pake navigate (?)
    }
}