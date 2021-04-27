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
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    val newsFragment = NewsFragment()
    val trainingFragment = TrainingFragment()
    val historyFragment = HistoryFragment()
    val scheduleFragment = ScheduleFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        makeCurrentFragment(newsFragment)
        binding.bottomNavbar.setOnItemSelectedListener {
            when(it) {
                R.id.nav_news -> makeCurrentFragment(newsFragment)
                R.id.nav_training -> makeCurrentFragment(trainingFragment)
                R.id.nav_history -> makeCurrentFragment(historyFragment)
                R.id.nav_schedule -> makeCurrentFragment(scheduleFragment)
            }
        }
    }

    fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}