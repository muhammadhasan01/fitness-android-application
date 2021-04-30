package com.k310.fitness.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k310.fitness.R
import com.k310.fitness.db.training.Training
import com.k310.fitness.ui.activities.LogDetailActivity
import com.k310.fitness.ui.historyList.HistoryAdapter
import com.k310.fitness.ui.viewmodels.MainViewModel
import com.k310.fitness.util.RecyclerItemClickListener
import com.k310.fitness.util.training.TrainingType
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class HistoryFragment : Fragment(), LifecycleOwner {
    // TODO: Rename and change types of parameters
    private val viewModel: MainViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var rvHistory: RecyclerView
    private lateinit var cvHistory: CalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvHistory = view.findViewById<View>(R.id.rvHistory) as RecyclerView
        historyAdapter = HistoryAdapter(emptyList())

        viewModel.getTrainingsByDate().observe(viewLifecycleOwner, { historyList ->
            historyAdapter = HistoryAdapter(historyList)
            rvHistory.adapter = historyAdapter
            rvHistory.layoutManager = LinearLayoutManager(activity)
            recyclerViewAddListener(rvHistory, historyList)
        })

        cvHistory = view.findViewById<View>(R.id.calendarView) as CalendarView
        cvHistory.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = 0
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
            }
            val timeInMs = calendar.timeInMillis
            val historyListLiveData =
                viewModel.getTrainingsByDate(timeInMs, timeInMs + 1000 * 60 * 60 * 24)
            historyListLiveData.observe(viewLifecycleOwner, { historyList ->
                historyAdapter = HistoryAdapter(historyList)
                rvHistory.adapter = historyAdapter
                rvHistory.layoutManager = LinearLayoutManager(activity)
                recyclerViewAddListener(rvHistory, historyList)
            })
        }
    }

    private inline fun recyclerViewAddListener(r : RecyclerView, l : List<Training>) {
        r.addOnItemTouchListener(
            RecyclerItemClickListener(
                activity,
                r,
                object : RecyclerItemClickListener.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        val history = l[position]
                        val intent = Intent(activity, LogDetailActivity::class.java).apply {
                            putExtra(
                                "trainingType",
                                if (history.trainingType == TrainingType.CYCLING) "Cycling" else "Running"
                            )
                            putExtra("startTime", Date(history.startTime.timeInMillis).toString())
                            putExtra("stopTime", Date(history.stopTime.timeInMillis).toString())
                            putExtra("detail", history.detail.toString())
                            putExtra("duration", history.duration.toString())
                            putExtra("avgInKmh", history.avgInKMH.toString())
                        }
                        startActivity(intent)
                    }
                }
            )
        )
    }
}