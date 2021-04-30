package com.k310.fitness.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.k310.fitness.R
import com.k310.fitness.databinding.ActivityMainBinding
import com.k310.fitness.ui.fragments.*
import com.k310.fitness.util.Constants.ACTION_SHOW_TRACKING_FRAGMENT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val newsFragment = NewsFragment()
    val trainingFragment = TrainingFragment()
    val trackingFragment = TrackingFragment()
    val counterFragment = CounterFragment()
    private val historyFragment = HistoryFragment()
    private val scheduleFragment = ScheduleFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        toTrackingFragment(intent)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        makeCurrentFragment(newsFragment)
        binding.bottomNavbar.setOnItemSelectedListener {
            when (it) {
                R.id.nav_news -> makeCurrentFragment(newsFragment)
                R.id.nav_training -> makeCurrentFragment(trainingFragment)
                R.id.nav_history -> makeCurrentFragment(historyFragment)
                R.id.nav_schedule -> makeCurrentFragment(scheduleFragment)
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null) {
            toTrackingFragment(intent)
        }
    }

    private fun toTrackingFragment(intent: Intent) {
        if (intent.action == ACTION_SHOW_TRACKING_FRAGMENT) {
            makeCurrentFragment(trackingFragment)
        }
    }

    fun toTrainingFragment() {
        makeCurrentFragment(trackingFragment)
    }

    fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.fl_wrapper, fragment)
            commit()
        }
}