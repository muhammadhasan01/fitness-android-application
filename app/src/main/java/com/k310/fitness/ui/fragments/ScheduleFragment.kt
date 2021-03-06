package com.k310.fitness.ui.fragments

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.k310.fitness.R
import com.k310.fitness.ui.dialogs.ScheduleDialogs
import com.k310.fitness.ui.schedulelist.ScheduleAdapter
import com.k310.fitness.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ScheduleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
class ScheduleFragment : Fragment(R.layout.fragment_schedule), LifecycleOwner {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private val viewModel: MainViewModel by viewModels()
    private lateinit var scheduleAdapter: ScheduleAdapter
    private lateinit var rvSchedules: RecyclerView
    private val TAG = "ScheduleFragment"

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
        createChannel(
            getString(R.string.channel_id),
            getString(R.string.channel_name)
        )

        return inflater.inflate(
            R.layout.fragment_schedule, container, false
        )
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.findViewById<Button>(R.id.schedule_add_btn).setOnClickListener {
            ScheduleDialogs(activity, viewModel).show()
        }

        rvSchedules = view.findViewById<View>(R.id.rvSchedule) as RecyclerView
        scheduleAdapter = ScheduleAdapter(emptyList())

        viewModel.getSchedules().observe(viewLifecycleOwner, { scheduleList ->
            scheduleAdapter = ScheduleAdapter(scheduleList)
            rvSchedules.adapter = scheduleAdapter
            rvSchedules.layoutManager = LinearLayoutManager(activity)
        })
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ScheduleFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ScheduleFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}