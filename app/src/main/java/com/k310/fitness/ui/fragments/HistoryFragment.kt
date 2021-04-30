package com.k310.fitness.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k310.fitness.R
import com.k310.fitness.ui.historyList.HistoryAdapter
import com.k310.fitness.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistoryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class HistoryFragment : Fragment(), LifecycleOwner {
    // TODO: Rename and change types of parameters
    private val viewModel: MainViewModel by viewModels()
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var rvHistory: RecyclerView
    private lateinit var cvHistory: CalendarView

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

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
            })
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HistoryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HistoryFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}